package org.dataki.infrastructure.adapter;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador de recordatorios en modo simulado (por defecto).
 * <p>
 * Activo con <code>app.notifications.mode=simulated</code> (o sin la propiedad).
 * Registra en el log el mensaje que se enviaría por WhatsApp sin realizar llamadas reales.
 */
@Component
@ConditionalOnProperty(name = "app.notifications.mode", havingValue = "simulated", matchIfMissing = true)
public class CitaProcessorAdapter extends AbstractCitaProcessor {

    public CitaProcessorAdapter(CitaRepository citaRepository) {
        super(citaRepository);
    }

    @Override
    public void processCita(Cita cita) {
        logger.info("[SIMULADO] Enviando recordatorio por WhatsApp a {} ({}): 'Hola {}, te recordamos tu cita de {} el {}'",
                cita.getCustomerName(), cita.getPhone(), cita.getCustomerName(), cita.getService(), formatFecha(cita));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
