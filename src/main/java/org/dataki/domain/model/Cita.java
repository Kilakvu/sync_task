package org.dataki.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa una cita agendada en la peluquería.
 * Además del ciclo de vida de negocio (SCHEDULED, ATTENDED, CANCELLED) se apoya
 * en el motor asíncrono de la plataforma para enviar recordatorios cuando la cita se acerca.
 */
public class Cita {
    private final String id;
    private final String customerName;
    private final String phone;
    private final String service;
    private final int durationMinutes;
    private final String notes;
    private CitaStatus status;
    private CitaPriority priority;
    private int retryCount;
    private final int maxRetries;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final LocalDateTime scheduledFor;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime attendedAt;
    private LocalDateTime cancelledAt;

    private Cita(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.customerName = builder.customerName;
        this.phone = builder.phone;
        this.service = builder.service;
        this.durationMinutes = builder.durationMinutes;
        this.notes = builder.notes;
        this.status = CitaStatus.SCHEDULED;
        this.priority = builder.priority != null ? builder.priority : CitaPriority.MEDIUM;
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
        this.status = CitaStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPending() {
        this.status = CitaStatus.PENDING;
        this.errorMessage = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marca la cita como procesada: el recordatorio fue enviado (o la franja ya pasó).
     */
    public void markAsCompleted() {
        this.status = CitaStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();

        if (this.retryCount < this.maxRetries) {
            this.status = CitaStatus.RETRY;
            this.retryCount++;
        } else {
            this.status = CitaStatus.FAILED;
        }
    }

    public void markAsScheduled() {
        this.status = CitaStatus.SCHEDULED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * El cliente fue atendido en la peluquería.
     */
    public void attend() {
        if (this.status == CitaStatus.CANCELLED) {
            throw new IllegalStateException("No se puede atender una cita cancelada");
        }
        this.status = CitaStatus.ATTENDED;
        this.attendedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * La cita fue cancelada (cliente llamó de vuelta).
     */
    public void cancel() {
        if (this.status == CitaStatus.CANCELLED || this.status == CitaStatus.ATTENDED) {
            throw new IllegalStateException("La cita ya está en un estado final: " + this.status);
        }
        this.status = CitaStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return this.retryCount < this.maxRetries && this.status == CitaStatus.RETRY;
    }

    public boolean isReadyToProcess() {
        return (this.status == CitaStatus.PENDING || this.status == CitaStatus.RETRY)
                && (this.scheduledFor == null || this.scheduledFor.isBefore(LocalDateTime.now()));
    }

    // Métodos para sincronizar desde persistencia
    public void syncFromPersistence(CitaStatus status, int retryCount, String errorMessage,
                                    LocalDateTime startedAt, LocalDateTime completedAt,
                                    LocalDateTime attendedAt, LocalDateTime cancelledAt) {
        this.status = status;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.attendedAt = attendedAt;
        this.cancelledAt = cancelledAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getService() {
        return service;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public CitaStatus getStatus() {
        return status;
    }

    public CitaPriority getPriority() {
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

    public LocalDateTime getAttendedAt() {
        return attendedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public static class Builder {
        private String id;
        private String customerName;
        private String phone;
        private String service;
        private int durationMinutes = 30;
        private String notes;
        private CitaPriority priority;
        private int maxRetries = 3;
        private LocalDateTime scheduledFor;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder priority(CitaPriority priority) {
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

        public Cita build() {
            if (this.customerName == null || this.customerName.isBlank()) {
                throw new IllegalArgumentException("El nombre del cliente es obligatorio");
            }
            if (this.phone == null || this.phone.isBlank()) {
                throw new IllegalArgumentException("El teléfono del cliente es obligatorio");
            }
            if (this.service == null || this.service.isBlank()) {
                throw new IllegalArgumentException("El servicio es obligatorio");
            }
            if (this.scheduledFor == null) {
                throw new IllegalArgumentException("La fecha y hora de la cita es obligatoria");
            }
            return new Cita(this);
        }
    }
}
