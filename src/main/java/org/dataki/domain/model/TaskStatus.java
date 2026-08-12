package org.dataki.domain.model;

/**
 * Estados posibles de una tarea en el sistema
 */
public enum TaskStatus {
    PENDING("Pendiente de procesar"),
    PROCESSING("En proceso"),
    COMPLETED("Completada"),
    FAILED("Fallida"),
    RETRY("En reintento"),
    SCHEDULED("Programada");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

