package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dataki.domain.model.TaskPriority;
import org.dataki.domain.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de tareas
 */
@Schema(description = "Respuesta con información completa de una tarea")
public record TaskResponse(
        @Schema(description = "Identificador único de la tarea", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        
        @Schema(description = "Nombre de la tarea", example = "Procesar Reporte")
        String name,
        
        @Schema(description = "Descripción de la tarea", example = "Generar reporte de ventas")
        String description,
        
        @Schema(description = "Datos JSON a procesar", example = "{\"month\": \"August\"}")
        String payload,
        
        @Schema(description = "Estado actual de la tarea", example = "PENDING")
        TaskStatus status,
        
        @Schema(description = "Nivel de prioridad", example = "HIGH")
        TaskPriority priority,
        
        @Schema(description = "Número de reintentos realizados", example = "0")
        int retryCount,
        
        @Schema(description = "Máximo de reintentos permitidos", example = "3")
        int maxRetries,
        
        @Schema(description = "Mensaje de error si aplica", example = "Connection timeout")
        String errorMessage,
        
        @Schema(description = "Fecha de creación", example = "2026-08-12T23:15:00")
        LocalDateTime createdAt,
        
        @Schema(description = "Fecha de última actualización", example = "2026-08-12T23:16:45")
        LocalDateTime updatedAt,
        
        @Schema(description = "Fecha programada de ejecución", example = "2026-08-13T02:00:00")
        LocalDateTime scheduledFor,
        
        @Schema(description = "Fecha de inicio del procesamiento", example = "2026-08-12T23:16:00")
        LocalDateTime startedAt,
        
        @Schema(description = "Fecha de finalización", example = "2026-08-12T23:16:45")
        LocalDateTime completedAt
) {
}

