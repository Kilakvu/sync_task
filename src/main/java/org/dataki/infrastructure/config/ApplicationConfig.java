package org.dataki.infrastructure.config;

import org.dataki.application.service.CreateTaskService;
import org.dataki.application.service.RetrieveTaskService;
import org.dataki.application.service.TaskProcessorService;
import org.dataki.domain.port.output.TaskProcessor;
import org.dataki.domain.port.output.TaskRepository;
import org.dataki.domain.service.TaskDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración de beans y aplicación
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ApplicationConfig {
    
    @Bean
    public TaskDomainService taskDomainService(TaskRepository taskRepository) {
        return new TaskDomainService(taskRepository);
    }

    @Bean
    public CreateTaskService createTaskService(TaskRepository taskRepository, TaskProcessor taskProcessor) {
        return new CreateTaskService(taskRepository, taskProcessor);
    }

    @Bean
    public RetrieveTaskService retrieveTaskService(TaskRepository taskRepository) {
        return new RetrieveTaskService(taskRepository);
    }

    @Bean
    public TaskProcessorService taskProcessorService(TaskRepository taskRepository) {
        return new TaskProcessorService(taskRepository);
    }
}

