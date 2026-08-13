package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dataki.domain.model.CitaPriority;
import org.dataki.domain.model.CitaStatus;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de citas
 */
@Schema(description = "Respuesta con información completa de una cita")
public record CitaResponse(
        @Schema(description = "Identificador único de la cita", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre del cliente", example = "María González")
        String customerName,

        @Schema(description = "Teléfono del cliente", example = "+34 612 345 678")
        String phone,

        @Schema(description = "Servicio contratado", example = "Corte y peinado")
        String service,

        @Schema(description = "Duración estimada en minutos", example = "30")
        int durationMinutes,

        @Schema(description = "Notas de la cita", example = "Cliente pide mechas")
        String notes,

        @Schema(description = "Estado actual de la cita", example = "SCHEDULED")
        CitaStatus status,

        @Schema(description = "Nivel de prioridad", example = "HIGH")
        CitaPriority priority,

        @Schema(description = "Número de reintentos realizados del recordatorio", example = "0")
        int retryCount,

        @Schema(description = "Máximo de reintentos permitidos", example = "3")
        int maxRetries,

        @Schema(description = "Mensaje de error si aplica", example = "Connection timeout")
        String errorMessage,

        @Schema(description = "Fecha de creación", example = "2026-08-13T23:15:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2026-08-13T23:16:45")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha y hora de la cita", example = "2026-08-14T10:30:00")
        LocalDateTime scheduledFor,

        @Schema(description = "Fecha de inicio del envío del recordatorio", example = "2026-08-14T10:29:00")
        LocalDateTime startedAt,

        @Schema(description = "Fecha en que se envió el recordatorio", example = "2026-08-14T10:29:05")
        LocalDateTime completedAt,

        @Schema(description = "Fecha en que el cliente fue atendido", example = "2026-08-14T10:32:00")
        LocalDateTime attendedAt,

        @Schema(description = "Fecha en que la cita fue cancelada", example = "2026-08-14T09:00:00")
        LocalDateTime cancelledAt
) {
}
