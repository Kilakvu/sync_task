package org.dataki.infrastructure.worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.dataki.infrastructure.persistence.entity.TaskEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Servicio que reclama (claim) tareas pendientes en Postgres usando
 * native query + FOR UPDATE SKIP LOCKED para evitar race conditions entre workers.
 */
@Service
public class TaskClaimService {
    private final EntityManager em;

    public TaskClaimService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public List<TaskEntity> claimPendingTasks(int limit, LocalDateTime now) {
        // Native query to fetch ids with FOR UPDATE SKIP LOCKED
        @SuppressWarnings("unchecked")
        List<String> ids = em.createNativeQuery(
                "SELECT id FROM tasks " +
                "WHERE status IN ('PENDING','RETRY') " +
                "AND (scheduled_for IS NULL OR scheduled_for <= :now) " +
                "ORDER BY priority ASC, created_at ASC " +
                "FOR UPDATE SKIP LOCKED LIMIT :lim")
                .setParameter("now", now)
                .setParameter("lim", limit)
                .getResultList();

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<TaskEntity> q = em.createQuery(
                "select t from TaskEntity t where t.id in :ids", TaskEntity.class);
        q.setParameter("ids", ids);
        q.setLockMode(LockModeType.PESSIMISTIC_WRITE);

        List<TaskEntity> entities = q.getResultList();

        // Mark them as PROCESSING and update timestamps
        for (TaskEntity e : entities) {
            e.setStatus(org.dataki.domain.model.TaskStatus.PROCESSING);
            e.setStartedAt(LocalDateTime.now());
            e.setUpdatedAt(LocalDateTime.now());
            em.merge(e);
        }

        return entities;
    }
}

