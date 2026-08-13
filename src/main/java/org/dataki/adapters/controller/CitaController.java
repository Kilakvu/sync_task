package org.dataki.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dataki.application.dto.CreateCitaBatchRequest;
import org.dataki.application.dto.CreateCitaRequest;
import org.dataki.application.dto.CitaResponse;
import org.dataki.application.mapper.CitaMapper;
import org.dataki.application.service.CreateCitaService;
import org.dataki.application.service.RetrieveCitaService;
import org.dataki.domain.model.Cita;
import org.dataki.domain.model.CitaStatus;
import org.dataki.domain.service.CitaDomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST para la API de citas de la peluquería
 */
@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "API para gestionar las citas de la peluquería")
public class CitaController {
    private final CreateCitaService createCitaService;
    private final RetrieveCitaService retrieveCitaService;
    private final CitaDomainService citaDomainService;

    public CitaController(CreateCitaService createCitaService, RetrieveCitaService retrieveCitaService,
                          CitaDomainService citaDomainService) {
        this.createCitaService = createCitaService;
        this.retrieveCitaService = retrieveCitaService;
        this.citaDomainService = citaDomainService;
    }

    /**
     * POST /api/v1/citas - Agendar una nueva cita
     */
    @PostMapping
    @Operation(summary = "Agendar nueva cita",
               description = "Agenda una cita en la peluquería; el recordatorio se enviará cuando llegue el momento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cita agendada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CitaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CitaResponse> createCita(@RequestBody CreateCitaRequest request) {
        Cita cita = createCitaService.createCita(
                request.customerName(),
                request.phone(),
                request.service(),
                request.durationMinutes() != null ? request.durationMinutes() : 30,
                request.notes(),
                request.priority(),
                request.maxRetries(),
                request.scheduledFor()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CitaMapper.toResponse(cita));
    }

    /**
     * POST /api/v1/citas/batch - Agendar múltiples citas de una sola vez
     */
    @PostMapping("/batch")
    @Operation(summary = "Agendar múltiples citas",
               description = "Agenda varias citas a la vez para observar el trabajo del motor de recordatorios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Citas agendadas exitosamente",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<CitaResponse>> createCitasBatch(@RequestBody CreateCitaBatchRequest request) {
        List<CitaResponse> citas = createCitaService.createCitas(request.citas())
                .stream()
                .map(CitaMapper::toResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(citas);
    }

    /**
     * GET /api/v1/citas/{id} - Obtener una cita por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID",
               description = "Recupera los detalles de una cita específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CitaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CitaResponse> getCitaById(
            @Parameter(description = "ID de la cita", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        return retrieveCitaService.getCitaById(id)
                .map(cita -> ResponseEntity.ok(CitaMapper.toResponse(cita)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/citas - Obtener todas las citas
     */
    @GetMapping
    @Operation(summary = "Listar todas las citas",
               description = "Recupera una lista de todas las citas en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de citas",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<CitaResponse>> getAllCitas() {
        List<CitaResponse> citas = retrieveCitaService.getAllCitas()
                .stream()
                .map(CitaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(citas);
    }

    /**
     * GET /api/v1/citas/hoy - Citas de hoy
     */
    @GetMapping("/hoy")
    @Operation(summary = "Citas de hoy",
               description = "Recupera las citas agendadas para el día actual")
    @ApiResponse(responseCode = "200", description = "Citas de hoy",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<CitaResponse>> getTodayCitas() {
        LocalDate today = LocalDate.now();
        List<CitaResponse> citas = retrieveCitaService.getCitasForDate(today.atStartOfDay(), today.atTime(LocalTime.MAX))
                .stream()
                .map(CitaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(citas);
    }

    /**
     * GET /api/v1/citas/status/{status} - Obtener citas por estado
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar citas por estado",
               description = "Recupera citas filtradas por su estado actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de citas filtradas",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<List<CitaResponse>> getCitasByStatus(
            @Parameter(description = "Estado de la cita (SCHEDULED, PENDING, PROCESSING, COMPLETED, ATTENDED, CANCELLED, RETRY, FAILED)",
                      example = "SCHEDULED")
            @PathVariable String status) {
        try {
            CitaStatus citaStatus = CitaStatus.valueOf(status.toUpperCase());
            List<CitaResponse> citas = retrieveCitaService.getCitasByStatus(citaStatus)
                    .stream()
                    .map(CitaMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(citas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/citas/queue/pending - Citas listas para recordatorio
     */
    @GetMapping("/queue/pending")
    @Operation(summary = "Ver cola de recordatorios",
               description = "Recupera citas que están listas para que se les envíe el recordatorio")
    @ApiResponse(responseCode = "200", description = "Cola de recordatorios",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<CitaResponse>> getReadyCitas() {
        List<CitaResponse> citas = retrieveCitaService.getCitasReadyToProcess()
                .stream()
                .map(CitaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(citas);
    }

    /**
     * PATCH /api/v1/citas/{id}/atender - Marcar cita como atendida
     */
    @PatchMapping("/{id}/atender")
    @Operation(summary = "Marcar cita como atendida",
                description = "El cliente llegó a la peluquería y fue atendido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita atendida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CitaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "409", description = "No se puede atender una cita cancelada")
    })
    public ResponseEntity<CitaResponse> attendCita(
            @Parameter(description = "ID de la cita", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        try {
            return ResponseEntity.ok(CitaMapper.toResponse(citaDomainService.attendCita(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * PATCH /api/v1/citas/{id}/cancelar - Cancelar una cita
     */
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita",
               description = "El cliente canceló la cita por teléfono")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cita cancelada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CitaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
        @ApiResponse(responseCode = "409", description = "La cita ya está en un estado final")
    })
    public ResponseEntity<CitaResponse> cancelCita(
            @Parameter(description = "ID de la cita", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        try {
            return ResponseEntity.ok(CitaMapper.toResponse(citaDomainService.cancelCita(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
