package org.dataki.application.service;

import org.dataki.application.dto.CreateCitaRequest;
import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaPriority;
import org.dataki.domain.port.input.CreateCitaUseCase;
import org.dataki.domain.port.output.CitaProcessor;
import org.dataki.domain.port.output.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso para agendar nuevas citas
 */
public class CreateCitaService implements CreateCitaUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CreateCitaService.class);
    private final CitaRepository citaRepository;
    private final CitaProcessor citaProcessor;

    public CreateCitaService(CitaRepository citaRepository, CitaProcessor citaProcessor) {
        this.citaRepository = citaRepository;
        this.citaProcessor = citaProcessor;
    }

    @Override
    public Cita createCita(String customerName, String phone, String service,
                           int durationMinutes, String notes,
                           CitaPriority priority, Integer maxRetries, LocalDateTime scheduledFor) {
        return createSingleCita(customerName, phone, service, durationMinutes, notes,
                priority, maxRetries, scheduledFor);
    }

    /**
     * Agenda múltiples citas de una sola vez
     */
    public List<Cita> createCitas(List<CreateCitaRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("La lista de citas no puede estar vacía");
        }
        logger.info("Agendando lote de {} citas", requests.size());
        return requests.stream()
                .map(r -> createSingleCita(r.customerName(), r.phone(), r.service(),
                        r.durationMinutes() != null ? r.durationMinutes() : 30,
                        r.notes(), r.priority(), r.maxRetries(), r.scheduledFor()))
                .toList();
    }

    private Cita createSingleCita(String customerName, String phone, String service,
                                  int durationMinutes, String notes,
                                  CitaPriority priority, Integer maxRetries, LocalDateTime scheduledFor) {
        logger.info("Agendando nueva cita para: {}", customerName);

        // Crear la cita usando el builder (validación en dominio)
        Cita cita = Cita.builder()
                .customerName(customerName)
                .phone(phone)
                .service(service)
                .durationMinutes(durationMinutes)
                .notes(notes)
                .priority(priority != null ? priority : CitaPriority.MEDIUM)
                .maxRetries(maxRetries != null ? maxRetries : 3)
                .scheduledFor(scheduledFor)
                .build();

        // Guardar la cita en el repositorio
        Cita savedCita = citaRepository.save(cita);
        logger.info("Cita agendada con id: {}", savedCita.getId());

        // Programar el recordatorio: el scheduler la recogerá cuando llegue el momento
        citaProcessor.scheduleCita(savedCita);

        return savedCita;
    }
}
