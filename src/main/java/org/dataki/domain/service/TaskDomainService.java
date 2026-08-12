package org.dataki.domain.service;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;
import org.dataki.domain.port.output.TaskRepository;

import java.util.List;

/**
 * Servicio de dominio para operaciones transversales con tareas
 */
public class TaskDomainService {
    private final TaskRepository taskRepository;

    public TaskDomainService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Obtiene todas las tareas listas para procesar
     */
    public List<Task> getReadyTasks() {
        return taskRepository.findPendingTasks();
    }

    /**
     * Actualiza el estado de una tarea
     */
    public Task updateTaskStatus(String taskId, TaskStatus status) {
        return taskRepository.findById(taskId)
                .map(task -> {
                    // La lógica específica de cambio de estado está en la entidad Task
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }

    /**
     * Obtiene tareas ordenadas por prioridad
     */
    public List<Task> getTasksByPriority() {
        List<Task> tasks = taskRepository.findAll();
        tasks.sort((t1, t2) -> Integer.compare(
                t1.getPriority().getValue(),
                t2.getPriority().getValue()
        ));
        return tasks;
    }
}

