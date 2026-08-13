package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dataki.domain.model.TaskPriority;

import java.time.LocalDateTime;

/**
 * DTO para solicitar la creación de una tarea
 */
@Schema(description = "Solicitud para crear una nueva tarea")
public record CreateTaskRequest(
        @Schema(description = "Nombre de la tarea", example = "Procesar Reporte", required = true)
        String name,
        
        @Schema(description = "Descripción detallada de la tarea", example = "Generar reporte de ventas")
        String description,
        
        @Schema(description = "Datos JSON para procesar", example = "{\"month\": \"August\", \"year\": 2026}", required = true)
        String payload,
        
        @Schema(description = "Nivel de prioridad", example = "HIGH", required = false)
        TaskPriority priority,
        
        @Schema(description = "Máximo número de reintentos", example = "3", required = false)
        Integer maxRetries,
        
        @Schema(description = "Fecha y hora de ejecución programada", example = "2026-08-13T02:00:00", required = false)
        LocalDateTime scheduledFor
) {
}

