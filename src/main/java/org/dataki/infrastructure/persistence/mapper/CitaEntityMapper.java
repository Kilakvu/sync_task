package org.dataki.infrastructure.persistence.mapper;

import org.dataki.domain.model.Cita;
import org.dataki.infrastructure.persistence.entity.CitaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Cita (dominio) y CitaEntity (persistencia)
 */
@Component
public class CitaEntityMapper {
    public CitaEntity toEntity(Cita cita) {
        return new CitaEntity(
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

    public Cita toDomain(CitaEntity entity) {
        Cita cita = Cita.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .phone(entity.getPhone())
                .service(entity.getService())
                .durationMinutes(entity.getDurationMinutes())
                .notes(entity.getNotes())
                .priority(entity.getPriority())
                .maxRetries(entity.getMaxRetries())
                .scheduledFor(entity.getScheduledFor())
                .build();

        // Sincronizar el estado desde la persistencia
        cita.syncFromPersistence(
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getAttendedAt(),
                entity.getCancelledAt()
        );

        return cita;
    }
}
