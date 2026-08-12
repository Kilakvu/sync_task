package org.dataki.application.dto;

import org.dataki.domain.model.TaskPriority;
import org.dataki.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO para solicitar la creación de una tarea
 */
public record CreateTaskRequest(
        String name,
        String description,
        String payload,
        TaskPriority priority,
        Integer maxRetries,
        LocalDateTime scheduledFor
) {
}

