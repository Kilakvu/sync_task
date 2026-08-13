package org.dataki.infrastructure.worker;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler que pone en la cola de recordatorios las citas programadas que ya llegaron a su hora
 */
@Component
public class ScheduledCitaScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledCitaScheduler.class);
    private final CitaRepository citaRepository;

    public ScheduledCitaScheduler(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    /**
     * Se ejecuta cada 10 segundos para encolar citas programadas que han llegado su momento
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 2000)
    public void processScheduledCitas() {
        logger.debug("ScheduledCitaScheduler: Buscando citas programadas listas...");

        List<Cita> allCitas = citaRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<Cita> readyScheduledCitas = allCitas.stream()
                .filter(cita -> cita.getStatus() == CitaStatus.SCHEDULED)
                .filter(cita -> cita.getScheduledFor() != null && cita.getScheduledFor().isBefore(now))
                .toList();

        if (!readyScheduledCitas.isEmpty()) {
            logger.info("ScheduledCitaScheduler: {} citas programadas listas para recordatorio", readyScheduledCitas.size());

            for (Cita cita : readyScheduledCitas) {
                processScheduledCita(cita);
            }
        }
    }

    private void processScheduledCita(Cita cita) {
        try {
            logger.info("Poniendo cita en cola de recordatorios: {}", cita.getId());
            cita.markAsPending();
            citaRepository.save(cita);
        } catch (Exception e) {
            logger.error("Error encolando cita: {}", cita.getId(), e);
        }
    }
}
