package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dataki.domain.model.CitaPriority;

import java.time.LocalDateTime;

/**
 * DTO para solicitar el agendamiento de una cita
 */
@Schema(description = "Solicitud para agendar una nueva cita")
public record CreateCitaRequest(
        @Schema(description = "Nombre del cliente", example = "María González", required = true)
        String customerName,

        @Schema(description = "Teléfono del cliente", example = "+34 612 345 678", required = true)
        String phone,

        @Schema(description = "Servicio contratado", example = "Corte y peinado", required = true)
        String service,

        @Schema(description = "Duración estimada en minutos", example = "30", required = false)
        Integer durationMinutes,

        @Schema(description = "Notas de la cita", example = "Cliente pide mechas y capas largas", required = false)
        String notes,

        @Schema(description = "Nivel de prioridad para el recordatorio", example = "HIGH", required = false)
        CitaPriority priority,

        @Schema(description = "Máximo número de reintentos del recordatorio", example = "3", required = false)
        Integer maxRetries,

        @Schema(description = "Fecha y hora de la cita", example = "2026-08-14T10:30:00", required = true)
        LocalDateTime scheduledFor
) {
}
