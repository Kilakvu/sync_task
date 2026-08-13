package org.dataki.domain.port.output;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define cómo persistir tareas
 */
public interface TaskRepository {
    Task save(Task task);

    Optional<Task> findById(String id);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findAll();

    List<Task> findPendingTasks();

    void delete(String id);

    boolean existsById(String id);
}
