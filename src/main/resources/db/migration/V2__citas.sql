-- Flyway Migration V2: Create citas table (sistema de citas de la peluquería)
-- Author: Ricardo Dataki
-- Date: 2026-08-13

CREATE TABLE IF NOT EXISTS citas (
    id VARCHAR(36) PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    service VARCHAR(255) NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 30,
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_for TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    attended_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

-- Índices para optimizar la cola de recordatorios y la agenda del día
CREATE INDEX IF NOT EXISTS idx_citas_status_scheduled_for
    ON citas(status, scheduled_for)
    WHERE status IN ('PENDING', 'RETRY');

CREATE INDEX IF NOT EXISTS idx_citas_priority_created_at
    ON citas(priority, created_at)
    WHERE status IN ('PENDING', 'RETRY');

CREATE INDEX IF NOT EXISTS idx_citas_scheduled_for
    ON citas(scheduled_for);

CREATE INDEX IF NOT EXISTS idx_citas_status
    ON citas(status);

-- Comentarios
COMMENT ON TABLE citas IS 'Tabla de citas agendadas en la peluquería';
COMMENT ON COLUMN citas.id IS 'Identificador único (UUID)';
COMMENT ON COLUMN citas.customer_name IS 'Nombre del cliente';
COMMENT ON COLUMN citas.phone IS 'Teléfono de contacto del cliente';
COMMENT ON COLUMN citas.service IS 'Servicio contratado (corte, tinte, peinado, etc.)';
COMMENT ON COLUMN citas.duration_minutes IS 'Duración estimada de la cita en minutos';
COMMENT ON COLUMN citas.status IS 'Estado: SCHEDULED, PENDING, PROCESSING, COMPLETED, ATTENDED, CANCELLED, RETRY, FAILED';
COMMENT ON COLUMN citas.priority IS 'Prioridad del recordatorio: CRITICAL(0), HIGH(1), MEDIUM(2), LOW(3)';
COMMENT ON COLUMN citas.retry_count IS 'Número de reintentos del recordatorio';
COMMENT ON COLUMN citas.max_retries IS 'Máximo de reintentos permitidos del recordatorio';
COMMENT ON COLUMN citas.scheduled_for IS 'Fecha y hora de la cita';
COMMENT ON COLUMN citas.completed_at IS 'Fecha en que se envió el recordatorio';
COMMENT ON COLUMN citas.attended_at IS 'Fecha en que el cliente fue atendido';
COMMENT ON COLUMN citas.cancelled_at IS 'Fecha en que la cita fue cancelada';
