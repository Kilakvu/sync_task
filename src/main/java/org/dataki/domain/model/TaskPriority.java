package org.dataki.domain.model;

/**
 * Niveles de prioridad para las tareas
 */
public enum TaskPriority {
    LOW(3),
    MEDIUM(2),
    HIGH(1),
    CRITICAL(0);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

