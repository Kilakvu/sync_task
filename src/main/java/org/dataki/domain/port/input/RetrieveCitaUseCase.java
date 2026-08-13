package org.dataki.domain.port.input;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para recuperar citas
 */
public interface RetrieveCitaUseCase {
    Optional<Cita> getCitaById(String id);

    List<Cita> getCitasByStatus(CitaStatus status);

    List<Cita> getAllCitas();

    List<Cita> getCitasReadyToProcess();

    List<Cita> getCitasForDate(LocalDateTime start, LocalDateTime end);
}
