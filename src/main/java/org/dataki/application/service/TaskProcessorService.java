package org.dataki.application.service;

import org.dataki.domain.model.Task;
import org.dataki.domain.port.output.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso para procesar tareas de forma sincrónica
 */
public class TaskProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorService.class);
    private final TaskRepository taskRepository;

    public TaskProcessorService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Procesa una tarea de forma sincrónica
     */
    public void executeTask(String taskId) {
        logger.info("Processing task with id: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        try {
            task.markAsProcessing();
            taskRepository.save(task);

            // Aquí iría la lógica real de procesamiento
            // Simulamos el procesamiento
            simulateTaskExecution(task);

            task.markAsCompleted();
            taskRepository.save(task);
            logger.info("Task completed successfully: {}", taskId);

        } catch (Exception e) {
            logger.error("Error processing task: {}", taskId, e);
            task.markAsFailed(e.getMessage());
            taskRepository.save(task);

            if (task.canRetry()) {
                logger.info("Task will be retried: {} (attempt {})", taskId, task.getRetryCount());
            }
        }
    }

    /**
     * Reintenta procesar una tarea fallida
     */
    public void retryTask(String taskId) {
        logger.info("Retrying task with id: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (!task.canRetry()) {
            throw new IllegalArgumentException("Task cannot be retried: " + taskId);
        }

        executeTask(taskId);
    }

    private void simulateTaskExecution(Task task) throws InterruptedException {
        // Simular procesamiento pesado
        Thread.sleep(1000);
    }
}
