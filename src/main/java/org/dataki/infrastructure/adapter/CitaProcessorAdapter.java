package org.dataki.infrastructure.adapter;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaProcessor;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de procesamiento que implementa el puerto CitaProcessor
 */
@Component
public class CitaProcessorAdapter implements CitaProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CitaProcessorAdapter.class);
    private final CitaRepository citaRepository;

    public CitaProcessorAdapter(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public void processCita(Cita cita) {
        logger.info("CitaProcessorAdapter: Encolando recordatorio de cita: {}", cita.getId());
        // El envío del recordatorio se hace mediante CitaWorker de forma asíncrona
        // Este adaptador simplemente marca que la cita debe procesarse
    }

    @Override
    public void scheduleCita(Cita cita) {
        logger.info("CitaProcessorAdapter: Programando recordatorio para la cita: {}", cita.getId());
        cita.markAsScheduled();
        citaRepository.save(cita);
        // El ScheduledCitaScheduler se encargará de poner la cita en la cola cuando llegue el momento
    }
}
