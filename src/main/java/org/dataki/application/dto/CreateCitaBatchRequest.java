package org.dataki.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO para solicitar el agendamiento masivo de citas
 */
@Schema(description = "Solicitud para agendar múltiples citas de una sola vez")
public record CreateCitaBatchRequest(
        @Schema(description = "Lista de citas a crear", required = true)
        List<CreateCitaRequest> citas
) {
}
