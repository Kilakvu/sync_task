package org.dataki.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.dataki.domain.model.TaskPriority;
import org.dataki.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia de tareas en base de datos
 */
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(nullable = false)
    private Integer maxRetries = 3;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime scheduledFor;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    // Constructores
    public TaskEntity() {
    }

    public TaskEntity(String id, String name, String description, String payload,
                      TaskStatus status, TaskPriority priority, Integer retryCount,
                      Integer maxRetries, String errorMessage, LocalDateTime createdAt,
                      LocalDateTime updatedAt, LocalDateTime scheduledFor,
                      LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.payload = payload;
        this.status = status;
        this.priority = priority;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scheduledFor = scheduledFor;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
