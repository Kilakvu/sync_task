package org.dataki.application.service;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;
import org.dataki.domain.port.input.RetrieveTaskUseCase;
import org.dataki.domain.port.output.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para recuperar tareas
 */
public class RetrieveTaskService implements RetrieveTaskUseCase {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveTaskService.class);
    private final TaskRepository taskRepository;

    public RetrieveTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        logger.debug("Retrieving task with id: {}", id);
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        logger.debug("Retrieving tasks with status: {}", status);
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> getAllTasks() {
        logger.debug("Retrieving all tasks");
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksReadyToProcess() {
        logger.debug("Retrieving ready tasks");
        return taskRepository.findPendingTasks();
    }
}
