package org.dataki.adapters.controller;

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
     * GET /api/v1/tasks/{id} - Obtener una tarea por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        return retrieveTaskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(TaskMapper.toResponse(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/tasks - Obtener todas las tareas
     */
    @GetMapping
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
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable String status) {
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
    public ResponseEntity<List<TaskResponse>> getReadyTasks() {
        List<TaskResponse> tasks = retrieveTaskService.getTasksReadyToProcess()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }
}


