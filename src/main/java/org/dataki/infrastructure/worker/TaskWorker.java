package org.dataki.infrastructure.worker;

import org.dataki.application.service.TaskProcessorService;
import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;
import org.dataki.domain.port.output.TaskRepository;
import org.dataki.infrastructure.persistence.entity.TaskEntity;
import org.dataki.infrastructure.worker.TaskClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Worker que procesa tareas de forma asíncrona
 */
@Component
public class TaskWorker {
    private static final Logger logger = LoggerFactory.getLogger(TaskWorker.class);
    private final TaskRepository taskRepository;
    private final TaskProcessorService processorService;
    private final TaskClaimService claimService;

    public TaskWorker(TaskRepository taskRepository, TaskProcessorService processorService, TaskClaimService claimService) {
        this.taskRepository = taskRepository;
        this.processorService = processorService;
        this.claimService = claimService;
    }

    /**
     * Se ejecuta cada 5 segundos para procesar tareas pendientes
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void processQueue() {
        logger.debug("TaskWorker: Checking for pending tasks...");
        
        // Primero intentamos reclamar tareas de forma atómica (Postgres: FOR UPDATE SKIP LOCKED)
        try {
            List<TaskEntity> claimed = claimService.claimPendingTasks(10, java.time.LocalDateTime.now());
            if (!claimed.isEmpty()) {
                logger.info("TaskWorker: Claimed {} tasks for processing", claimed.size());
                for (TaskEntity e : claimed) {
                    processPendingTask(e.getId());
                }
                return;
            }
        } catch (Exception ex) {
            logger.debug("TaskWorker: claim failed (falling back to polling): {}", ex.getMessage());
        }

        // Fallback: simple polling (H2 or environments without SKIP LOCKED)
        List<Task> pendingTasks = taskRepository.findPendingTasks();
        if (!pendingTasks.isEmpty()) {
            logger.info("TaskWorker: Found {} pending tasks (fallback)", pendingTasks.size());
            for (Task task : pendingTasks) {
                processPendingTask(task.getId());
            }
        }
    }

    /**
     * Procesa una tarea de forma asíncrona
     */
    @Async
    protected void processPendingTask(String taskId) {
        try {
            processorService.executeTask(taskId);
        } catch (Exception e) {
            logger.error("Error in async task processing for task: {}", taskId, e);
        }
    }

    /**
     * Se ejecuta cada minuto para procesar tareas que fallaron y necesitan reintentó
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void processRetries() {
        logger.debug("TaskWorker: Checking for tasks that need retry...");
        
        List<Task> retryTasks = taskRepository.findByStatus(TaskStatus.RETRY);
        
        if (!retryTasks.isEmpty()) {
            logger.info("TaskWorker: Found {} tasks for retry", retryTasks.size());
            
            for (Task task : retryTasks) {
                retryTask(task.getId());
            }
        }
    }

    /**
     * Reintenta una tarea de forma asíncrona
     */
    @Async
    protected void retryTask(String taskId) {
        try {
            processorService.retryTask(taskId);
        } catch (Exception e) {
            logger.error("Error retrying task: {}", taskId, e);
        }
    }
}

