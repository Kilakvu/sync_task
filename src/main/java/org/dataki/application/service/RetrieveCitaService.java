package org.dataki.application.service;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;
import org.dataki.domain.port.input.RetrieveCitaUseCase;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para recuperar citas
 */
public class RetrieveCitaService implements RetrieveCitaUseCase {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveCitaService.class);
    private final CitaRepository citaRepository;

    public RetrieveCitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public Optional<Cita> getCitaById(String id) {
        logger.debug("Recuperando cita con id: {}", id);
        return citaRepository.findById(id);
    }

    @Override
    public List<Cita> getCitasByStatus(CitaStatus status) {
        logger.debug("Recuperando citas con estado: {}", status);
        return citaRepository.findByStatus(status);
    }

    @Override
    public List<Cita> getAllCitas() {
        logger.debug("Recuperando todas las citas");
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> getCitasReadyToProcess() {
        logger.debug("Recuperando citas listas para recordatorio");
        return citaRepository.findPendingCitas();
    }

    @Override
    public List<Cita> getCitasForDate(LocalDateTime start, LocalDateTime end) {
        logger.debug("Recuperando citas entre {} y {}", start, end);
        return citaRepository.findByScheduledForBetween(start, end);
    }
}
