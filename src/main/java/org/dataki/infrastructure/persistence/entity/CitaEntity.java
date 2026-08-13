package org.dataki.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.dataki.domain.model.CitaPriority;
import org.dataki.domain.model.CitaStatus;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistencia de citas en base de datos
 */
@Entity
@Table(name = "citas")
public class CitaEntity {
    @Id
    private String id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String service;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 30;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CitaStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CitaPriority priority;

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

    @Column(name = "scheduled_for", nullable = false)
    private LocalDateTime scheduledFor;

    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "attended_at")
    private LocalDateTime attendedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Constructores
    public CitaEntity() {
    }

    public CitaEntity(String id, String customerName, String phone, String service,
                      Integer durationMinutes, String notes, CitaStatus status,
                      CitaPriority priority, Integer retryCount, Integer maxRetries,
                      String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt,
                      LocalDateTime scheduledFor, LocalDateTime startedAt, LocalDateTime completedAt,
                      LocalDateTime attendedAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.service = service;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
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
        this.attendedAt = attendedAt;
        this.cancelledAt = cancelledAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CitaStatus getStatus() {
        return status;
    }

    public void setStatus(CitaStatus status) {
        this.status = status;
    }

    public CitaPriority getPriority() {
        return priority;
    }

    public void setPriority(CitaPriority priority) {
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

    public LocalDateTime getAttendedAt() {
        return attendedAt;
    }

    public void setAttendedAt(LocalDateTime attendedAt) {
        this.attendedAt = attendedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
