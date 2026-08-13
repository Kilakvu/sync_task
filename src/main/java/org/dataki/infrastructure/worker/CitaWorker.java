package org.dataki.infrastructure.worker;

import org.dataki.application.service.CitaProcessorService;
import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;
import org.dataki.domain.port.output.CitaRepository;
import org.dataki.infrastructure.persistence.entity.CitaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Worker que envía recordatorios de forma asíncrona
 */
@Component
public class CitaWorker {
    private static final Logger logger = LoggerFactory.getLogger(CitaWorker.class);
    private final CitaRepository citaRepository;
    private final CitaProcessorService processorService;
    private final CitaClaimService claimService;

    public CitaWorker(CitaRepository citaRepository, CitaProcessorService processorService,
                      CitaClaimService claimService) {
        this.citaRepository = citaRepository;
        this.processorService = processorService;
        this.claimService = claimService;
    }

    /**
     * Se ejecuta cada 5 segundos para encolar recordatorios pendientes
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void processQueue() {
        logger.debug("CitaWorker: Buscando recordatorios pendientes...");

        // Primero intentamos reclamar citas de forma atómica (Postgres: FOR UPDATE SKIP LOCKED)
        try {
            List<CitaEntity> claimed = claimService.claimPendingCitas(10, LocalDateTime.now());
            if (!claimed.isEmpty()) {
                logger.info("CitaWorker: Reclamadas {} citas para recordatorio", claimed.size());
                for (CitaEntity e : claimed) {
                    processPendingCita(e.getId());
                }
                return;
            }
        } catch (Exception ex) {
            logger.debug("CitaWorker: claim fallido (usando polling): {}", ex.getMessage());
        }

        // Fallback: simple polling (H2 o entornos sin SKIP LOCKED)
        List<Cita> pendingCitas = citaRepository.findPendingCitas();
        if (!pendingCitas.isEmpty()) {
            logger.info("CitaWorker: {} recordatorios pendientes (fallback)", pendingCitas.size());
            for (Cita cita : pendingCitas) {
                processPendingCita(cita.getId());
            }
        }
    }

    /**
     * Procesa el recordatorio de una cita de forma asíncrona
     */
    @Async
    protected void processPendingCita(String citaId) {
        try {
            processorService.executeCita(citaId);
        } catch (Exception e) {
            logger.error("Error en el envío asíncrono del recordatorio de la cita: {}", citaId, e);
        }
    }

    /**
     * Se ejecuta cada minuto para reintentar recordatorios que fallaron
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void processRetries() {
        logger.debug("CitaWorker: Buscando recordatorios que necesitan reintento...");

        List<Cita> retryCitas = citaRepository.findByStatus(CitaStatus.RETRY);

        if (!retryCitas.isEmpty()) {
            logger.info("CitaWorker: {} recordatorios para reintento", retryCitas.size());

            for (Cita cita : retryCitas) {
                retryCita(cita.getId());
            }
        }
    }

    /**
     * Reintenta un recordatorio de forma asíncrona
     */
    @Async
    protected void retryCita(String citaId) {
        try {
            processorService.retryCita(citaId);
        } catch (Exception e) {
            logger.error("Error reintentando recordatorio: {}", citaId, e);
        }
    }
}
