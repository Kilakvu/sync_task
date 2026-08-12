package org.dataki.infrastructure.persistence.repository;

import org.dataki.infrastructure.persistence.entity.TaskEntity;
import org.dataki.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para acceso a datos de tareas
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, String> {
    List<TaskEntity> findByStatus(TaskStatus status);

    @Query("SELECT t FROM TaskEntity t WHERE t.status IN ('PENDING', 'RETRY') " +
            "AND (t.scheduledFor IS NULL OR t.scheduledFor <= CURRENT_TIMESTAMP) " +
            "ORDER BY t.priority ASC, t.createdAt ASC")
    List<TaskEntity> findPendingTasksOrderByPriority();

    @Query("SELECT t FROM TaskEntity t WHERE t.status = 'SCHEDULED' AND t.scheduledFor <= ?1 " +
            "ORDER BY t.scheduledFor ASC")
    List<TaskEntity> findScheduledTasksReadyToProcess(LocalDateTime now);
}

