package org.dataki.infrastructure.worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.dataki.domain.model.CitaStatus;
import org.dataki.infrastructure.persistence.entity.CitaEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Servicio que reclama (claim) citas pendientes de recordatorio en Postgres usando
 * native query + FOR UPDATE SKIP LOCKED para evitar race conditions entre workers.
 */
@Service
public class CitaClaimService {
    private final EntityManager em;

    public CitaClaimService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public List<CitaEntity> claimPendingCitas(int limit, LocalDateTime now) {
        // Native query para obtener ids con FOR UPDATE SKIP LOCKED
        @SuppressWarnings("unchecked")
        List<String> ids = em.createNativeQuery(
                "SELECT id FROM citas " +
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

        TypedQuery<CitaEntity> q = em.createQuery(
                "select c from CitaEntity c where c.id in :ids", CitaEntity.class);
        q.setParameter("ids", ids);
        q.setLockMode(LockModeType.PESSIMISTIC_WRITE);

        List<CitaEntity> entities = q.getResultList();

        // Marcarlas como PROCESSING y actualizar timestamps
        for (CitaEntity e : entities) {
            e.setStatus(CitaStatus.PROCESSING);
            e.setStartedAt(LocalDateTime.now());
            e.setUpdatedAt(LocalDateTime.now());
            em.merge(e);
        }

        return entities;
    }
}
