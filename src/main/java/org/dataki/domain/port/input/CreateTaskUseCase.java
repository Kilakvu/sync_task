package org.dataki.domain.port.input;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskPriority;

import java.time.LocalDateTime;

/**
 * Puerto de entrada para crear nuevas tareas
 */
public interface CreateTaskUseCase {
    Task createTask(String name, String description, String payload, 
                    TaskPriority priority, Integer maxRetries, LocalDateTime scheduledFor);
}

