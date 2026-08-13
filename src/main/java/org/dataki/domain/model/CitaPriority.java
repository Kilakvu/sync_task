package org.dataki.domain.model;

/**
 * Niveles de prioridad para las citas (determinan el orden de envío de recordatorios)
 */
public enum CitaPriority {
    LOW(3),
    MEDIUM(2),
    HIGH(1),
    CRITICAL(0);

    private final int value;

    CitaPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
