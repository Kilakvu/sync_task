package org.dataki.domain.port.input;

import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaPriority;

import java.time.LocalDateTime;

/**
 * Puerto de entrada para agendar nuevas citas
 */
public interface CreateCitaUseCase {
    Cita createCita(String customerName, String phone, String service,
                    int durationMinutes, String notes,
                    CitaPriority priority, Integer maxRetries, LocalDateTime scheduledFor);
}
