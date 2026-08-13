package org.dataki.domain.port.input;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para recuperar tareas
 */
public interface RetrieveTaskUseCase {
    Optional<Task> getTaskById(String id);

    List<Task> getTasksByStatus(TaskStatus status);

    List<Task> getAllTasks();

    List<Task> getTasksReadyToProcess();
}
