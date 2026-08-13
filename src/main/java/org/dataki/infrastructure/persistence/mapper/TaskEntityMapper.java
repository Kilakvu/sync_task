package org.dataki.infrastructure.persistence.mapper;

import org.dataki.domain.model.Task;
import org.dataki.infrastructure.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Task (dominio) y TaskEntity (persistencia)
 */
@Component
public class TaskEntityMapper {
    public TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getPayload(),
                task.getStatus(),
                task.getPriority(),
                task.getRetryCount(),
                task.getMaxRetries(),
                task.getErrorMessage(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getScheduledFor(),
                task.getStartedAt(),
                task.getCompletedAt()
        );
    }

    public Task toDomain(TaskEntity entity) {
        Task task = Task.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .payload(entity.getPayload())
                .priority(entity.getPriority())
                .maxRetries(entity.getMaxRetries())
                .scheduledFor(entity.getScheduledFor())
                .build();

        // Sincronizar el estado desde la persistencia
        task.syncFromPersistence(
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );

        return task;
    }
}

