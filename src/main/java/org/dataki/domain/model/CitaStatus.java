package org.dataki.domain.model;

/**
 * Estados posibles de una cita en el sistema.
 * Los estados de negocio son SCHEDULED, ATTENDED y CANCELLED; el resto pertenecen
 * al motor de recordatorios asíncrono (cola de procesamiento).
 */
public enum CitaStatus {
    SCHEDULED("Programada"),
    PENDING("Pendiente de recordatorio"),
    PROCESSING("Enviando recordatorio"),
    COMPLETED("Recordatorio enviado"),
    ATTENDED("Atendida"),
    CANCELLED("Cancelada"),
    RETRY("Recordatorio en reintento"),
    FAILED("Recordatorio fallido");

    private final String description;

    CitaStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
