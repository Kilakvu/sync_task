package org.dataki.domain.port.output;

import org.dataki.domain.model.Cita;

/**
 * Puerto de salida que define cómo procesar citas (envío de recordatorios)
 */
public interface CitaProcessor {
    void processCita(Cita cita);

    void scheduleCita(Cita cita);
}
