# Sistema de Procesamiento Asíncrono de Tareas - Arquitectura Hexagonal

Un sistema empresarial de procesamiento de tareas asíncronas con arquitectura hexagonal, que implementa mejores prácticas de diseño de software.

## 🏗️ Arquitectura

El proyecto implementa la arquitectura hexagonal (puertos y adaptadores) dividida en capas:

```
┌─────────────────────────────────────────────────────────┐
│                    ADAPTADORES DE ENTRADA               │
│              (REST Controllers)                         │
└──────────────────────┬──────────────────────────────────┘
                       │
┌─────────────────────────────────────────────────────────┐
│                   CAPA DE APLICACIÓN                     │
│         (Servicios y Casos de Uso)                      │
│    - CreateTaskService                                  │
│    - RetrieveTaskService                                │
│    - TaskProcessorService                               │
└──────────────────────┬──────────────────────────────────┘
                       │
┌─────────────────────────────────────────────────────────┐
│    PUERTOS (Interfaces - Input & Output)                │
│    - CreateTaskUseCase                                  │
│    - RetrieveTaskUseCase                                │
│    - TaskRepository                                     │
│    - TaskProcessor                                      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌─────────────────────────────────────────────────────────┐
│              CAPA DE DOMINIO                             │
│    - Entidades (Task)                                   │
│    - Enums (TaskStatus, TaskPriority)                   │
│    - Servicios de Dominio                               │
└──────────────────────┬──────────────────────────────────┘
                       │
┌─────────────────────────────────────────────────────────┐
│           ADAPTADORES DE SALIDA                          │
│    - TaskRepositoryAdapter (JPA)                        │
│    - TaskProcessorAdapter                               │
│    - TaskWorker (Procesamiento Asíncrono)               │
│    - ScheduledTaskScheduler                             │
└─────────────────────────────────────────────────────────┘
```

## 🎯 Características

### 1. **Gestión de Tareas**
- ✅ Crear tareas con payload personalizado
- ✅ Estados de tarea: PENDING, PROCESSING, COMPLETED, FAILED, RETRY, SCHEDULED
- ✅ Recuperar tareas por ID, estado o listar todas

### 2. **Sistema de Prioridades**
- ✅ 4 niveles: CRITICAL (0), HIGH (1), MEDIUM (2), LOW (3)
- ✅ Las tareas se procesan ordenadas por prioridad

### 3. **Reintentos Automáticos**
- ✅ Configurable por tarea (por defecto 3 intentos)
- ✅ Reintento automático tras fallos
- ✅ Manejo de errores con captura de excepción

### 4. **Tareas Programadas**
- ✅ Crear tareas para ejecutar en un momento específico
- ✅ Scheduler que valida cada 10 segundos las tareas listas
- ✅ Transición automática de SCHEDULED a PENDING

### 5. **Procesamiento Asíncrono**
- ✅ Worker que procesa la cola cada 5 segundos
- ✅ Procesamiento en thread pool asíncrono
- ✅ Retry worker que valida cada 60 segundos

### 6. **Manejo de Errores**
- ✅ Captura detallada de excepciones
- ✅ Logging completo en todos los niveles
- ✅ Estados finales claros (COMPLETED o FAILED)

## 📊 Estructura de Capas

### Domain Layer (`org.dataki.domain`)
- **Models**: Task, TaskStatus, TaskPriority
- **Ports**: Interfaces que definen los contratos
- **Services**: Lógica de negocio transversal

### Application Layer (`org.dataki.application`)
- **DTOs**: Objetos de transferencia de datos
- **Services**: Implementación de casos de uso
- **Mappers**: Conversión entre capas

### Adapters Layer (`org.dataki.adapters`)
- **Controllers**: REST API endpoints

### Infrastructure Layer (`org.dataki.infrastructure`)
- **Persistence**: JPA/Hibernate mappings y repositories
- **Workers**: Procesamiento asíncrono
- **Scheduler**: Tareas programadas
- **Config**: Configuración Spring

## 🚀 Como Iniciar

### Requisitos
- Java 21+
- Maven 3.9+

### Compilar
```bash
mvn clean compile
```

### Ejecutar
```bash
mvn spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080`

## 📡 API REST Endpoints

### Crear una Tarea
```
POST /api/v1/tasks
Content-Type: application/json

{
  "name": "Procesar Reporte",
  "description": "Generar reporte de ventas",
  "payload": "{\"month\": \"August\", \"year\": 2026}",
  "priority": "HIGH",
  "maxRetries": 3,
  "scheduledFor": "2026-08-12T23:30:00"
}
```

**Respuesta (201 Created)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Procesar Reporte",
  "description": "Generar reporte de ventas",
  "payload": "{\"month\": \"August\", \"year\": 2026}",
  "status": "SCHEDULED",
  "priority": "HIGH",
  "retryCount": 0,
  "maxRetries": 3,
  "errorMessage": null,
  "createdAt": "2026-08-12T23:15:00",
  "updatedAt": "2026-08-12T23:15:00",
  "scheduledFor": "2026-08-12T23:30:00",
  "startedAt": null,
  "completedAt": null
}
```

### Obtener Tarea por ID
```
GET /api/v1/tasks/{id}
```

### Obtener Todas las Tareas
```
GET /api/v1/tasks
```

### Obtener Tareas por Estado
```
GET /api/v1/tasks/status/{status}
```

Estados válidos: PENDING, PROCESSING, COMPLETED, FAILED, RETRY, SCHEDULED

### Obtener Tareas Listas para Procesar
```
GET /api/v1/tasks/queue/pending
```

## 📋 Ejemplos de Uso

### 1. Crear una Tarea Inmediata
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enviar Email",
    "description": "Enviar confirmación a usuario",
    "payload": "{\"email\": \"user@example.com\"}",
    "priority": "MEDIUM"
  }'
```

### 2. Crear una Tarea Programada
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Generar Backup",
    "description": "Backup diario de BD",
    "payload": "{\"database\": \"main\"}",
    "priority": "CRITICAL",
    "scheduledFor": "2026-08-13T02:00:00",
    "maxRetries": 5
  }'
```

### 3. Consultar Tareas Fallidas
```bash
curl http://localhost:8080/api/v1/tasks/status/FAILED
```

### 4. Ver Cola de Procesamiento
```bash
curl http://localhost:8080/api/v1/tasks/queue/pending
```

## 🔄 Flujo de Procesamiento

```
1. Cliente POST /tasks
                 ↓
2. CreateTaskService.createTask()
                 ↓
3. Task guardada en BD (estado PENDING)
                 ↓
4. TaskWorker (cada 5s) busca nuevas tareas
                 ↓
5. Ejecuta tarea de forma asíncrona
                 ↓
6a. Si éxito: COMPLETED        6b. Si error: FAILED o RETRY
                 ↓                        ↓
           Guardada en BD     TaskWorker reintentos (cada 60s)
                                        ↓
                                     Si agota reintentos: FAILED
```

## 🗄️ Base de Datos

Usa H2 en memoria para desarrollo:
- **URL**: jdbc:h2:mem:taskdb
- **Console H2**: http://localhost:8080/h2-console

### Tabla Tasks
```sql
CREATE TABLE tasks (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    payload LONGTEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    scheduled_for TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);
```

## 🧪 Testing

El proyecto incluye dependencias de testing:
- JUnit 5
- Mockito
- AssertJ

Para ejecutar tests:
```bash
mvn test
```

## 📝 Logging

Niveles configurados en `application.yml`:
- **ROOT**: INFO
- **org.dataki**: DEBUG (Detalle completo)
- **org.springframework.web**: INFO
- **Hibernate SQL**: DEBUG

## 🎓 Patrones y Mejores Prácticas

1. **Arquitectura Hexagonal**: Independencia de frameworks
2. **Domain-Driven Design**: Lógica de negocio centralizada
3. **Builder Pattern**: Construcción segura de objetos
4. **Dependency Injection**: Spring Autoconfiguration
5. **Async Programming**: Workers y Schedulers
6. **Error Handling**: Manejo granular de excepciones
7. **Logging**: Trazabilidad completa

## 🔮 Mejoras Futuras

- [ ] Mensaje broker (RabbitMQ, Kafka)
- [ ] Persistencia con PostgreSQL
- [ ] Métricas Prometheus
- [ ] Trazas distribuidas (Jaeger)
- [ ] GraphQL API
- [ ] Autenticación/Autorización
- [ ] Rate Limiting
- [ ] Circuit Breaker
- [ ] Websockets para eventos en tiempo real

## 📄 Licencia

MIT License

## 👤 Autor

Para portafolio backend - 2026

