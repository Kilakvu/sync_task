package org.dataki.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dataki.application.dto.CreateTaskBatchRequest;
import org.dataki.application.dto.CreateTaskRequest;
import org.dataki.application.dto.TaskResponse;
import org.dataki.application.mapper.TaskMapper;
import org.dataki.application.service.CreateTaskService;
import org.dataki.application.service.RetrieveTaskService;
import org.dataki.domain.model.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la API de tareas
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "API para gestionar tareas de procesamiento asíncrono")
public class TaskController {
    private final CreateTaskService createTaskService;
    private final RetrieveTaskService retrieveTaskService;

    public TaskController(CreateTaskService createTaskService, RetrieveTaskService retrieveTaskService) {
        this.createTaskService = createTaskService;
        this.retrieveTaskService = retrieveTaskService;
    }

    /**
     * POST /api/v1/tasks - Crear una nueva tarea
     */
    @PostMapping
    @Operation(summary = "Crear nueva tarea", 
               description = "Crea una nueva tarea en el sistema que será procesada asíncronamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        var task = createTaskService.createTask(
                request.name(),
                request.description(),
                request.payload(),
                request.priority(),
                request.maxRetries(),
                request.scheduledFor()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper.toResponse(task));
    }

    /**
     * POST /api/v1/tasks/batch - Crear múltiples tareas de una sola vez
     */
    @PostMapping("/batch")
    @Operation(summary = "Crear múltiples tareas",
               description = "Crea varias tareas a la vez para saturar la cola de procesamiento y observar el trabajo de los workers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tareas creadas exitosamente",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TaskResponse>> createTasksBatch(@RequestBody CreateTaskBatchRequest request) {
        List<TaskResponse> tasks = createTaskService.createTasks(request.tasks())
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(tasks);
    }

    /**
     * GET /api/v1/tasks/{id} - Obtener una tarea por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarea por ID", 
               description = "Recupera los detalles de una tarea específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarea encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tarea no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "ID de la tarea", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        return retrieveTaskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(TaskMapper.toResponse(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/tasks - Obtener todas las tareas
     */
    @GetMapping
    @Operation(summary = "Listar todas las tareas", 
               description = "Recupera una lista de todas las tareas en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de tareas",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = retrieveTaskService.getAllTasks()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/v1/tasks/status/{status} - Obtener tareas por estado
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar tareas por estado", 
               description = "Recupera tareas filtradas por su estado actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tareas filtradas",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Estado de la tarea (PENDING, PROCESSING, COMPLETED, FAILED, RETRY, SCHEDULED)",
                      example = "PENDING")
            @PathVariable String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            List<TaskResponse> tasks = retrieveTaskService.getTasksByStatus(taskStatus)
                    .stream()
                    .map(TaskMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/tasks/queue/pending - Obtener tareas listas para procesar
     */
    @GetMapping("/queue/pending")
    @Operation(summary = "Ver cola de procesamiento", 
               description = "Recupera tareas que están listas para ser procesadas")
    @ApiResponse(responseCode = "200", description = "Cola de procesamiento",
                content = @Content(mediaType = "application/json"))
    public ResponseEntity<List<TaskResponse>> getReadyTasks() {
        List<TaskResponse> tasks = retrieveTaskService.getTasksReadyToProcess()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }
}



