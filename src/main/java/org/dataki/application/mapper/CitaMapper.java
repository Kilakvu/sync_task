package org.dataki.application.mapper;

import org.dataki.application.dto.CitaResponse;
import org.dataki.domain.model.Cita;

/**
 * Mapper para convertir entre Cita (dominio) y CitaResponse (DTO)
 */
public class CitaMapper {
    public static CitaResponse toResponse(Cita cita) {
        return new CitaResponse(
                cita.getId(),
                cita.getCustomerName(),
                cita.getPhone(),
                cita.getService(),
                cita.getDurationMinutes(),
                cita.getNotes(),
                cita.getStatus(),
                cita.getPriority(),
                cita.getRetryCount(),
                cita.getMaxRetries(),
                cita.getErrorMessage(),
                cita.getCreatedAt(),
                cita.getUpdatedAt(),
                cita.getScheduledFor(),
                cita.getStartedAt(),
                cita.getCompletedAt(),
                cita.getAttendedAt(),
                cita.getCancelledAt()
        );
    }
}
