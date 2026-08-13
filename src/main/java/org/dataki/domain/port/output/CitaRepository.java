package org.dataki.domain.port.output;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define cómo persistir citas
 */
public interface CitaRepository {
    Cita save(Cita cita);

    Optional<Cita> findById(String id);

    List<Cita> findByStatus(CitaStatus status);

    List<Cita> findAll();

    List<Cita> findPendingCitas();

    List<Cita> findByScheduledForBetween(LocalDateTime start, LocalDateTime end);

    void delete(String id);

    boolean existsById(String id);
}
