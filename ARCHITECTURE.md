# Arquitectura Hexagonal - Guía Detallada

## Introducción

Este documento describe la implementación de la arquitectura hexagonal en el sistema de procesamiento de tareas. La arquitectura hexagonal (también conocida como "puertos y adaptadores") es un patrón arquitectónico que favorece la independencia de frameworks e implementaciones específicas.

## Principios Fundamentales

### 1. **Aislamiento del Dominio**
- La lógica de negocio está completamente aislada de los detalles de implementación
- El dominio no depende de frameworks externos
- Los cambios en tecnología no afectan la lógica de negocio

### 2. **Inversión de Dependencias**
- Las dependencias apuntan hacia adentro (hacia el dominio)
- Las implementaciones dependen de interfaces, no al revés
- El dominio define los contratos (puertos)

### 3. **Separación de Responsabilidades**
- Cada capa tiene responsabilidades claras
- Las capas están desacopladas entre sí
- La comunicación entre capas se realiza a través de interfaces

## Estructura del Proyecto

```
org.dataki/
├── domain/                           # NÚCLEO DE NEGOCIO
│   ├── model/
│   │   ├── Task.java
│   │   ├── TaskStatus.java
│   │   └── TaskPriority.java
│   ├── port/
│   │   ├── input/                    # PUERTOS DE ENTRADA
│   │   │   ├── CreateTaskUseCase.java
│   │   │   └── RetrieveTaskUseCase.java
│   │   └── output/                   # PUERTOS DE SALIDA
│   │       ├── TaskRepository.java
│   │       └── TaskProcessor.java
│   └── service/
│       └── TaskDomainService.java
│
├── application/                      # CAPA DE APLICACIÓN
│   ├── dto/
│   │   ├── CreateTaskRequest.java
│   │   └── TaskResponse.java
│   ├── mapper/
│   │   └── TaskMapper.java
│   └── service/
│       ├── CreateTaskService.java
│       ├── RetrieveTaskService.java
│       └── TaskProcessorService.java
│
├── adapters/                         # ADAPTADORES DE ENTRADA
│   └── controller/
│       └── TaskController.java
│
└── infrastructure/                   # IMPLEMENTACIONES
    ├── persistence/
    │   ├── entity/
    │   │   └── TaskEntity.java
    │   ├── repository/
    │   │   └── TaskJpaRepository.java
    │   ├── adapter/
    │   │   └── TaskRepositoryAdapter.java
    │   └── mapper/
    │       └── TaskEntityMapper.java
    ├── worker/
    │   ├── TaskWorker.java
    │   └── ScheduledTaskScheduler.java
    ├── adapter/
    │   └── TaskProcessorAdapter.java
    └── config/
        ├── ApplicationConfig.java
        └── JpaConfig.java
```

## Descripción de Capas

### 1. Domain Layer (org.dataki.domain)

**Responsabilidad**: Contiene la lógica de negocio pura, independiente de frameworks.

#### Modelos de Dominio
```java
// Task - Entidad de dominio con lógica de negocio
public class Task {
    private String id;
    private String name;
    private TaskStatus status;
    private TaskPriority priority;
    
    // Métodos de negocio
    public void markAsProcessing() { ... }
    public void markAsCompleted() { ... }
    public void markAsFailed(String error) { ... }
    public boolean canRetry() { ... }
}

// TaskStatus y TaskPriority - Enums que definen el estado y prioridad
```

#### Puertos (Interfaces)

Los puertos son interfaces que definen los contratos:

**Input Ports** (casos de uso que el dominio expone):
```java
public interface CreateTaskUseCase {
    Task createTask(String name, String description, String payload, 
                    TaskPriority priority, Integer maxRetries, LocalDateTime scheduledFor);
}

public interface RetrieveTaskUseCase {
    Optional<Task> getTaskById(String id);
    List<Task> getTasksByStatus(TaskStatus status);
    // ... más métodos
}
```

**Output Ports** (abstracciones de servicios externos):
```java
public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    // ... más métodos
}

public interface TaskProcessor {
    void processTask(Task task);
    void scheduleTask(Task task);
}
```

#### Servicios de Dominio

Lógica que no pertenece a una entidad específica:
```java
public class TaskDomainService {
    public List<Task> getReadyTasks() { ... }
    public List<Task> getTasksByPriority() { ... }
}
```

### 2. Application Layer (org.dataki.application)

**Responsabilidad**: Coordinar casos de uso, convertir entre capas, validaciones.

#### DTOs (Data Transfer Objects)
```java
// Entrada (desde el cliente)
public record CreateTaskRequest(
    String name,
    String description,
    String payload,
    TaskPriority priority,
    Integer maxRetries,
    LocalDateTime scheduledFor
) { }

// Salida (hacia el cliente)
public record TaskResponse(
    String id,
    String name,
    TaskStatus status,
    // ... más campos
) { }
```

#### Servicios de Aplicación

Implementan los puertos de entrada:
```java
@Service
public class CreateTaskService implements CreateTaskUseCase {
    private final TaskRepository taskRepository;
    private final TaskProcessor taskProcessor;
    
    @Override
    public Task createTask(String name, String description, ...) {
        // Orquestación del caso de uso
        Task task = Task.builder()...build();
        Task saved = taskRepository.save(task);
        taskProcessor.processTask(saved);
        return saved;
    }
}
```

#### Mappers

Convierten entre capas:
```java
public class TaskMapper {
    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(...);
    }
}
```

### 3. Adapters Layer (org.dataki.adapters)

**Responsabilidad**: Puntos de entrada/salida de la aplicación.

#### Input Adapters (Controllers)
```java
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final CreateTaskService createTaskService;
    private final RetrieveTaskService retrieveTaskService;
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        Task task = createTaskService.createTask(...);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TaskMapper.toResponse(task));
    }
}
```

### 4. Infrastructure Layer (org.dataki.infrastructure)

**Responsabilidad**: Implementaciones técnicas concretas.

#### Output Adapters (Persistencia)

Implementan los puertos de salida:
```java
@Component
public class TaskRepositoryAdapter implements TaskRepository {
    private final TaskJpaRepository jpaRepository;
    
    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

#### Workers (Procesamiento Asíncrono)
```java
@Component
public class TaskWorker {
    @Scheduled(fixedDelay = 5000)
    public void processQueue() {
        List<Task> pending = taskRepository.findPendingTasks();
        for (Task task : pending) {
            this.processPendingTask(task.getId());
        }
    }
}
```

#### Configuración
```java
@Configuration
@EnableScheduling
@EnableAsync
public class ApplicationConfig {
    @Bean
    public CreateTaskService createTaskService(...) {
        return new CreateTaskService(...);
    }
}
```

## Flujo de Datos

### 1. Crear una Tarea

```
Cliente HTTP
    ↓
TaskController.createTask(CreateTaskRequest)
    ↓
CreateTaskService.createTask(...)
    ↓
Task.builder().build()
    ↓
TaskRepositoryAdapter.save(Task)
    ↓
TaskJpaRepository.save(TaskEntity)
    ↓
H2 Database
    ↓
TaskProcessorAdapter.processTask(Task)
    ↓
TaskWorker.processQueue() (cada 5s)
    ↓
TaskProcessorService.executeTask(String taskId)
```

### 2. Recuperar una Tarea

```
Cliente HTTP
    ↓
TaskController.getTaskById(String id)
    ↓
RetrieveTaskService.getTaskById(id)
    ↓
TaskRepositoryAdapter.findById(id)
    ↓
TaskJpaRepository.findById(id)
    ↓
H2 Database
    ↓
TaskEntityMapper.toDomain(TaskEntity)
    ↓
TaskResponse
```

## Patrones Utilizados

### 1. **Builder Pattern**
Construcción segura y legible de objetos complejos:
```java
Task task = Task.builder()
    .name("Mi Tarea")
    .payload("{...}")
    .priority(TaskPriority.HIGH)
    .maxRetries(5)
    .build();
```

### 2. **Adapter Pattern**
Adaptadores que implementan los puertos:
```java
public class TaskRepositoryAdapter implements TaskRepository { ... }
```

### 3. **Dependency Injection**
Inyección de dependencias via Spring:
```java
@Service
public class CreateTaskService {
    private final TaskRepository repository;
    
    public CreateTaskService(TaskRepository repository) {
        this.repository = repository;
    }
}
```

### 4. **Strategy Pattern**
Diferentes estrategias de procesamiento:
```java
public interface TaskProcessor {
    void processTask(Task task);
    void scheduleTask(Task task);
}
```

### 5. **Observer Pattern**
Events y scheduling:
```java
@Scheduled(fixedDelay = 5000)
public void checkForNewTasks() { ... }
```

## Ventajas de esta Arquitectura

### 1. **Testabilidad**
- El dominio se prueba sin dependencias externas
- Los adaptadores se prueban con mocks
- No se necesita base de datos para pruebas de dominio

### 2. **Mantenibilidad**
- Los cambios en tecnología no afectan el negocio
- El código es más fácil de entender
- Las responsabilidades están claras

### 3. **Escalabilidad**
- Fácil agregar nuevos adaptadores (GraphQL, gRPC, etc.)
- Fácil cambiar implementaciones (BD, queue, etc.)
- Permite crecimiento del equipo

### 4. **Flexibilidad**
- Intercambiar H2 por PostgreSQL sin cambiar dominio
- Agregar RabbitMQ sin afectar lógica de negocio
- Implementar caché de forma aislada

## Extensiones Futuras

### 1. **Agregar GraphQL**
Nuevo adapter de entrada:
```
graphql/
├── Query.graphql
├── Mutation.graphql
└── GraphQLController.java
```

### 2. **Event Sourcing**
Nuevo adapter de salida:
```
event/
├── TaskEventStore.java
├── TaskEventPublisher.java
└── EventSourcingAdapter.java
```

### 3. **Message Queue**
```
messaging/
├── RabbitMQTaskPublisher.java
├── RabbitMQTaskConsumer.java
└── MessagingAdapter.java
```

### 4. **Autenticación**
```
auth/
├── SecurityConfig.java
├── JwtFilter.java
└── AuthService.java
```

## Testing Strategy

### Unit Tests (Dominio)
```java
@Test
public void testTaskMarkAsCompleted() {
    Task task = Task.builder()...build();
    task.markAsCompleted();
    assertEquals(TaskStatus.COMPLETED, task.getStatus());
}
```

### Integration Tests (Adaptadores)
```java
@SpringBootTest
public class TaskControllerIntegrationTest {
    @Autowired
    private TestRestTemplate rest;
    
    @Test
    public void testCreateTask() {
        // Prueba el flujo completo
    }
}
```

## Conclusión

Esta arquitectura proporciona un balance entre flexibilidad, mantenibilidad y escalabilidad, siendo ideal para sistemas que deben evolucionar y adaptarse a nuevos requisitos sin comprometer la lógica de negocio existente.

