package org.dataki.infrastructure.adapter;

import org.dataki.domain.model.Task;
import org.dataki.domain.port.output.TaskProcessor;
import org.dataki.domain.port.output.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de procesamiento que implementa el puerto TaskProcessor
 */
@Component
public class TaskProcessorAdapter implements TaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorAdapter.class);
    private final TaskRepository taskRepository;

    public TaskProcessorAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void processTask(Task task) {
        logger.info("TaskProcessorAdapter: Enqueuing task for processing: {}", task.getId());
        // Las tareas se procesan mediante TaskWorker de forma asíncrona
        // Este adaptador simplemente marca que la tarea debe procesarse
    }

    @Override
    public void scheduleTask(Task task) {
        logger.info("TaskProcessorAdapter: Scheduling task for later execution: {}", task.getId());
        task.markAsScheduled();
        taskRepository.save(task);
        // El ScheduledTaskScheduler se encargará de procesar las tareas cuando llegue el momento
    }
}

