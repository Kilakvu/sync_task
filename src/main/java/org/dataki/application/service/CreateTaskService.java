package org.dataki.application.service;

import org.dataki.application.dto.CreateTaskRequest;
import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskPriority;
import org.dataki.domain.port.input.CreateTaskUseCase;
import org.dataki.domain.port.output.TaskProcessor;
import org.dataki.domain.port.output.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para crear nuevas tareas
 */
public class CreateTaskService implements CreateTaskUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CreateTaskService.class);
    private final TaskRepository taskRepository;
    private final TaskProcessor taskProcessor;

    public CreateTaskService(TaskRepository taskRepository, TaskProcessor taskProcessor) {
        this.taskRepository = taskRepository;
        this.taskProcessor = taskProcessor;
    }

    @Override
    public Task createTask(String name, String description, String payload, 
                          TaskPriority priority, Integer maxRetries, LocalDateTime scheduledFor) {
        return createSingleTask(name, description, payload, priority, maxRetries, scheduledFor);
    }

    /**
     * Crea múltiples tareas de una sola vez
     */
    public List<Task> createTasks(List<CreateTaskRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Task list cannot be empty");
        }
        logger.info("Creating batch of {} tasks", requests.size());
        return requests.stream()
                .map(r -> createSingleTask(r.name(), r.description(), r.payload(),
                        r.priority(), r.maxRetries(), r.scheduledFor()))
                .toList();
    }

    private Task createSingleTask(String name, String description, String payload,
                                  TaskPriority priority, Integer maxRetries, LocalDateTime scheduledFor) {
        logger.info("Creating new task: {}", name);

        // Validación en capa de aplicación
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }

        // Crear la tarea usando el constructor anidado (builder pattern)
        Task task = Task.builder()
                .name(name)
                .description(description)
                .payload(payload)
                .priority(priority != null ? priority : TaskPriority.MEDIUM)
                .maxRetries(maxRetries != null ? maxRetries : 3)
                .scheduledFor(scheduledFor)
                .build();

        // Guardar la tarea en el repositorio
        Task savedTask = taskRepository.save(task);
        logger.info("Task created with id: {}", savedTask.getId());

        // Procesar según si está programada o no
        if (savedTask.getScheduledFor() != null) {
            taskProcessor.scheduleTask(savedTask);
        } else {
            taskProcessor.processTask(savedTask);
        }

        return savedTask;
    }
}


