package org.dataki.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa una tarea en el sistema
 */
public class Task {
    private final String id;
    private final String name;
    private final String description;
    private final String payload;
    private TaskStatus status;
    private TaskPriority priority;
    private int retryCount;
    private final int maxRetries;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledFor;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private Task(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.name = builder.name;
        this.description = builder.description;
        this.payload = builder.payload;
        this.status = TaskStatus.PENDING;
        this.priority = builder.priority != null ? builder.priority : TaskPriority.MEDIUM;
        this.retryCount = 0;
        this.maxRetries = builder.maxRetries;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.scheduledFor = builder.scheduledFor;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Métodos de negocio
    public void markAsProcessing() {
        this.status = TaskStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();

        if (this.retryCount < this.maxRetries) {
            this.status = TaskStatus.RETRY;
            this.retryCount++;
        } else {
            this.status = TaskStatus.FAILED;
        }
    }

    public void markAsScheduled() {
        this.status = TaskStatus.SCHEDULED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return this.retryCount < this.maxRetries && this.status == TaskStatus.RETRY;
    }

    public boolean isReadyToProcess() {
        return (this.status == TaskStatus.PENDING || this.status == TaskStatus.RETRY)
                && (this.scheduledFor == null || this.scheduledFor.isBefore(LocalDateTime.now()));
    }

    // Métodos para sincronizar desde persistencia
    public void syncFromPersistence(TaskStatus status, int retryCount, String errorMessage,
                                   LocalDateTime startedAt, LocalDateTime completedAt) {
        this.status = status;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPayload() {
        return payload;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String payload;
        private TaskPriority priority;
        private int maxRetries = 3;
        private LocalDateTime scheduledFor;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder scheduledFor(LocalDateTime scheduledFor) {
            this.scheduledFor = scheduledFor;
            return this;
        }

        public Task build() {
            if (this.name == null || this.name.isBlank()) {
                throw new IllegalArgumentException("Task name is required");
            }
            if (this.payload == null || this.payload.isBlank()) {
                throw new IllegalArgumentException("Task payload is required");
            }
            return new Task(this);
        }
    }
}

