package org.dataki.infrastructure.adapter;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaProcessor;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

/**
 * Base común para los adaptadores de recordatorios.
 * El agendamiento (scheduleCita) y el formateo de la fecha son idénticos
 * para todos los canales (simulado, WhatsApp, futuro email/SMS).
 */
public abstract class AbstractCitaProcessor implements CitaProcessor {
    protected static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final CitaRepository citaRepository;

    protected AbstractCitaProcessor(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public void scheduleCita(Cita cita) {
        logger.info("Programando recordatorio para la cita: {}", cita.getId());
        cita.markAsScheduled();
        citaRepository.save(cita);
    }

    protected String formatFecha(Cita cita) {
        return cita.getScheduledFor() != null ? cita.getScheduledFor().format(FECHA_FORMATTER) : "-";
    }
}
