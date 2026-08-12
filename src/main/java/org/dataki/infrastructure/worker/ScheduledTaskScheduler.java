package org.dataki.infrastructure.worker;

import org.dataki.domain.model.Task;
import org.dataki.domain.model.TaskStatus;
import org.dataki.domain.port.output.TaskProcessor;
import org.dataki.domain.port.output.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler para procesar tareas programadas (scheduled)
 */
@Component
public class ScheduledTaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskScheduler.class);
    private final TaskRepository taskRepository;
    private final TaskProcessor taskProcessor;

    public ScheduledTaskScheduler(TaskRepository taskRepository, TaskProcessor taskProcessor) {
        this.taskRepository = taskRepository;
        this.taskProcessor = taskProcessor;
    }

    /**
     * Se ejecuta cada 10 segundos para procesar tareas programadas que han llegado su momento
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 2000)
    public void processScheduledTasks() {
        logger.debug("ScheduledTaskScheduler: Checking for ready scheduled tasks...");
        
        List<Task> allTasks = taskRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        List<Task> readyScheduledTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.SCHEDULED)
                .filter(task -> task.getScheduledFor() != null && task.getScheduledFor().isBefore(now))
                .toList();
        
        if (!readyScheduledTasks.isEmpty()) {
            logger.info("ScheduledTaskScheduler: Found {} ready scheduled tasks", readyScheduledTasks.size());
            
            for (Task task : readyScheduledTasks) {
                processScheduledTask(task);
            }
        }
    }

    private void processScheduledTask(Task task) {
        try {
            logger.info("Processing scheduled task: {}", task.getId());
            taskProcessor.processTask(task);
        } catch (Exception e) {
            logger.error("Error processing scheduled task: {}", task.getId(), e);
        }
    }
}

