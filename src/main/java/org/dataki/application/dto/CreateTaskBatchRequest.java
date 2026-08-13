package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO para solicitar la creación masiva de tareas
 */
@Schema(description = "Solicitud para crear múltiples tareas de una sola vez")
public record CreateTaskBatchRequest(
        @Schema(description = "Lista de tareas a crear", required = true)
        List<CreateTaskRequest> tasks
) {
}
