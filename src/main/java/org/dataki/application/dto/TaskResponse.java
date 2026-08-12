package org.dataki.application.dto;

import org.dataki.domain.model.TaskPriority;
import org.dataki.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de tareas
 */
public record TaskResponse(
        String id,
        String name,
        String description,
        String payload,
        TaskStatus status,
        TaskPriority priority,
        int retryCount,
        int maxRetries,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime scheduledFor,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}

