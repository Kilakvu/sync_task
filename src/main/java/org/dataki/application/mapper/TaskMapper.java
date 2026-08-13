package org.dataki.application.mapper;

import org.dataki.application.dto.TaskResponse;
import org.dataki.domain.model.Task;

/**
 * Mapper para convertir entre Task (dominio) y TaskResponse (DTO)
 */
public class TaskMapper {
    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
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
}
