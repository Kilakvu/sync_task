package org.dataki.domain.service;

import org.dataki.domain.model.Cita;
import org.dataki.domain.port.output.CitaRepository;

import java.util.List;

/**
 * Servicio de dominio para operaciones transversales con citas
 */
public class CitaDomainService {
    private final CitaRepository citaRepository;

    public CitaDomainService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    /**
     * Obtiene todas las citas listas para enviar recordatorio
     */
    public List<Cita> getReadyCitas() {
        return citaRepository.findPendingCitas();
    }

    /**
     * Obtiene citas ordenadas por prioridad
     */
    public List<Cita> getCitasByPriority() {
        List<Cita> citas = citaRepository.findAll();
        citas.sort((c1, c2) -> Integer.compare(
                c1.getPriority().getValue(),
                c2.getPriority().getValue()
        ));
        return citas;
    }

    /**
     * Marca una cita como atendida
     */
    public Cita attendCita(String citaId) {
        return citaRepository.findById(citaId)
                .map(cita -> {
                    cita.attend();
                    return citaRepository.save(cita);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + citaId));
    }

    /**
     * Cancela una cita
     */
    public Cita cancelCita(String citaId) {
        return citaRepository.findById(citaId)
                .map(cita -> {
                    cita.cancel();
                    return citaRepository.save(cita);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + citaId));
    }
}
