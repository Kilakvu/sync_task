package org.dataki.application.service;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaProcessor;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso para procesar citas (envío de recordatorios) de forma sincrónica.
 * Delega el envío al adaptador de canal activo (simulado o WhatsApp).
 */
public class CitaProcessorService {
    private static final Logger logger = LoggerFactory.getLogger(CitaProcessorService.class);
    private final CitaRepository citaRepository;
    private final CitaProcessor citaProcessor;

    public CitaProcessorService(CitaRepository citaRepository, CitaProcessor citaProcessor) {
        this.citaRepository = citaRepository;
        this.citaProcessor = citaProcessor;
    }

    /**
     * Procesa una cita de forma sincrónica: envía el recordatorio por el canal activo.
     */
    public void executeCita(String citaId) {
        logger.info("Procesando recordatorio de cita con id: {}", citaId);

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + citaId));

        try {
            cita.markAsProcessing();
            citaRepository.save(cita);

            citaProcessor.processCita(cita);

            cita.markAsCompleted();
            citaRepository.save(cita);
            logger.info("Recordatorio enviado correctamente: {}", citaId);

        } catch (Exception e) {
            logger.error("Error enviando recordatorio: {}", citaId, e);
            cita.markAsFailed(e.getMessage());
            citaRepository.save(cita);

            if (cita.canRetry()) {
                logger.info("El recordatorio será reintentado: {} (intento {})", citaId, cita.getRetryCount());
            }
        }
    }

    /**
     * Reintenta enviar un recordatorio que falló
     */
    public void retryCita(String citaId) {
        logger.info("Reintentando recordatorio de cita con id: {}", citaId);

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + citaId));

        if (!cita.canRetry()) {
            throw new IllegalArgumentException("La cita no se puede reintentar: " + citaId);
        }

        executeCita(citaId);
    }
}
