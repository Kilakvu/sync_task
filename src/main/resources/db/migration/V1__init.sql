-- Flyway Migration V1: Create tasks table
-- Author: Ricardo Dataki
-- Date: 2026-08-13

CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_for TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- Índices para optimizar queries de polling
CREATE INDEX IF NOT EXISTS idx_tasks_status_scheduled_for 
    ON tasks(status, scheduled_for) 
    WHERE status IN ('PENDING', 'RETRY');

CREATE INDEX IF NOT EXISTS idx_tasks_priority_created_at 
    ON tasks(priority, created_at) 
    WHERE status IN ('PENDING', 'RETRY');

CREATE INDEX IF NOT EXISTS idx_tasks_status 
    ON tasks(status);

-- Comentarios
COMMENT ON TABLE tasks IS 'Tabla de tareas para procesamiento asíncrono';
COMMENT ON COLUMN tasks.id IS 'Identificador único (UUID)';
COMMENT ON COLUMN tasks.status IS 'Estado: PENDING, PROCESSING, COMPLETED, FAILED, RETRY, SCHEDULED';
COMMENT ON COLUMN tasks.priority IS 'Prioridad: CRITICAL(0), HIGH(1), MEDIUM(2), LOW(3)';
COMMENT ON COLUMN tasks.retry_count IS 'Número de reintentos ejecutados';
COMMENT ON COLUMN tasks.max_retries IS 'Máximo de reintentos permitidos';
COMMENT ON COLUMN tasks.scheduled_for IS 'Fecha de ejecución programada (NULL = inmediata)';
