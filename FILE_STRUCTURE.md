# Estructura del Proyecto

## 📁 Árbol de Directorios

```
sync_task/
│
├── 📄 pom.xml                          # Configuración Maven y dependencias
├── 📄 Dockerfile                       # Imagen Docker multi-stage
├── 📄 docker-compose.yml               # Orquestación con PostgreSQL
│
├── 📄 README.md                        # Documentación principal
├── 📄 ARCHITECTURE.md                  # Guía de arquitectura hexagonal
├── 📄 EXAMPLES.md                      # Ejemplos de uso de API
├── 📄 QUICKSTART.md                    # Inicio rápido
├── 📄 CONTRIBUTING.md                  # Guía de contribución
├── 📄 PROJECT_SUMMARY.md               # Este archivo
│
├── 📁 src/
│   │
│   ├── 📁 main/
│   │   │
│   │   ├── 📁 java/org/dataki/
│   │   │   │
│   │   │   ├── 📁 domain/              ← CAPA DE DOMINIO
│   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── Task.java
│   │   │   │   │   ├── TaskStatus.java
│   │   │   │   │   └── TaskPriority.java
│   │   │   │   │
│   │   │   │   ├── 📁 port/
│   │   │   │   │   ├── 📁 input/
│   │   │   │   │   │   ├── CreateTaskUseCase.java
│   │   │   │   │   │   └── RetrieveTaskUseCase.java
│   │   │   │   │   └── 📁 output/
│   │   │   │   │       ├── TaskRepository.java
│   │   │   │   │       └── TaskProcessor.java
│   │   │   │   │
│   │   │   │   └── 📁 service/
│   │   │   │       └── TaskDomainService.java
│   │   │   │
│   │   │   ├── 📁 application/         ← CAPA DE APLICACIÓN
│   │   │   │   ├── 📁 dto/
│   │   │   │   │   ├── CreateTaskRequest.java
│   │   │   │   │   └── TaskResponse.java
│   │   │   │   │
│   │   │   │   ├── 📁 mapper/
│   │   │   │   │   └── TaskMapper.java
│   │   │   │   │
│   │   │   │   └── 📁 service/
│   │   │   │       ├── CreateTaskService.java
│   │   │   │       ├── RetrieveTaskService.java
│   │   │   │       └── TaskProcessorService.java
│   │   │   │
│   │   │   ├── 📁 adapters/            ← ADAPTADORES DE ENTRADA
│   │   │   │   └── 📁 controller/
│   │   │   │       └── TaskController.java
│   │   │   │
│   │   │   ├── 📁 infrastructure/      ← CAPA DE INFRAESTRUCTURA
│   │   │   │   ├── 📁 persistence/
│   │   │   │   │   ├── 📁 entity/
│   │   │   │   │   │   └── TaskEntity.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   │   └── TaskJpaRepository.java
│   │   │   │   │   ├── 📁 adapter/
│   │   │   │   │   │   └── TaskRepositoryAdapter.java
│   │   │   │   │   └── 📁 mapper/
│   │   │   │   │       └── TaskEntityMapper.java
│   │   │   │   │
│   │   │   │   ├── 📁 worker/
│   │   │   │   │   ├── TaskWorker.java
│   │   │   │   │   └── ScheduledTaskScheduler.java
│   │   │   │   │
│   │   │   │   ├── 📁 adapter/
│   │   │   │   │   └── TaskProcessorAdapter.java
│   │   │   │   │
│   │   │   │   └── 📁 config/
│   │   │   │       ├── ApplicationConfig.java
│   │   │   │       └── JpaConfig.java
│   │   │   │
│   │   │   └── 📄 Main.java            ← Punto de entrada
│   │   │
│   │   └── 📁 resources/
│   │       └── 📄 application.yml      # Configuración Spring Boot
│   │
│   └── 📁 test/                        # Tests (estructura preparada)
│       └── 📁 java/org/dataki/
│
└── 📁 target/                          # Compilados (generado por Maven)
    └── 📄 sync_task-1.0-SNAPSHOT.jar   # Aplicación empaquetada
```

## 📊 Estadísticas

| Métrica | Valor |
|---------|-------|
| Archivos Java | 25 |
| Archivos Markdown | 6 |
| Líneas de Código | ~2,500+ |
| Paquetes | 10 |
| Clases | 25 |
| Interfaces | 4 |
| Enums | 2 |

## 🗂️ Descripción de Carpetas

### 📁 domain/
**Responsabilidad**: Lógica de negocio pura
- **model/**: Entidades y value objects
- **port/**: Interfaces de puertos (input/output)
- **service/**: Servicios de dominio

### 📁 application/
**Responsabilidad**: Orquestación de casos de uso
- **dto/**: Objetos de transferencia de datos
- **mapper/**: Conversiones entre capas
- **service/**: Implementación de casos de uso

### 📁 adapters/
**Responsabilidad**: Puntos de entrada (API)
- **controller/**: REST API endpoints

### 📁 infrastructure/
**Responsabilidad**: Implementaciones técnicas
- **persistence/**: Acceso a datos (JPA)
- **worker/**: Procesamiento asíncrono
- **adapter/**: Adaptadores de salida
- **config/**: Configuración Spring

## 🔗 Relaciones Entre Archivos

```
TaskController
    ↓
CreateTaskService (implements CreateTaskUseCase)
    ↓ (uses)
    ├─ TaskRepository (interface)
    │   ↓ (implements)
    │   └─ TaskRepositoryAdapter
    │       ↓ (uses)
    │       └─ TaskJpaRepository
    │
    └─ TaskProcessor (interface)
        ↓ (implements)
        └─ TaskProcessorAdapter

TaskWorker
    ↓ (uses)
    ├─ TaskRepository
    └─ TaskProcessorService

ScheduledTaskScheduler
    ↓ (uses)
    ├─ TaskRepository
    └─ TaskProcessor
```

## 📝 Archivos Importante para Comenzar

### 1. **Para Entender la Arquitectura**
   - Leer: `ARCHITECTURE.md`
   - Revisar: `domain/port/input/CreateTaskUseCase.java`
   - Revisar: `domain/port/output/TaskRepository.java`

### 2. **Para Ver Ejemplos**
   - Leer: `EXAMPLES.md`
   - Revisar: `adapters/controller/TaskController.java`

### 3. **Para Desarrollar**
   - Copiar: `application/service/CreateTaskService.java` (patrón)
   - Seguir: `CONTRIBUTING.md`

### 4. **Para Ejecutar**
   - Leer: `QUICKSTART.md`
   - Revisar: `pom.xml` (dependencias)
   - Revisar: `src/main/resources/application.yml` (config)

## 🔀 Flujo de Datos (Ejemplo: Crear Tarea)

```
1. HTTP POST /api/v1/tasks
        ↓
2. TaskController.createTask(CreateTaskRequest)
        ↓
3. CreateTaskService.createTask(...) implements CreateTaskUseCase
        ↓
4. Task.builder().build() [Dominio]
        ↓
5. TaskRepositoryAdapter.save(Task)
        ↓
6. TaskJpaRepository.save(TaskEntity)
        ↓
7. H2 Database
        ↓
8. TaskProcessorAdapter.processTask(Task)
        ↓
9. TaskResponse [DTO]
        ↓
10. HTTP 201 Created
```

## 🎯 Puntos de Extensión (Fácil agregar)

### Agregar nuevo Adapter de Entrada (GraphQL)
```
adapters/
├── controller/
│   ├── TaskController.java (REST)
│   └── TaskGraphQLResolver.java (NUEVO)
```

### Agregar nuevo Adapter de Salida (RabbitMQ)
```
infrastructure/
├── adapter/
│   ├── TaskProcessorAdapter.java (En memoria)
│   └── RabbitMQAdapter.java (NUEVO)
```

### Cambiar de H2 a PostgreSQL
```
infrastructure/
└── persistence/
    └── [Sin cambios en código, solo config]
```

## 💾 Requisitos de Sistema

| Componente | Requerimiento |
|-----------|--------------|
| Java | 21+ |
| Maven | 3.9+ |
| RAM | 512MB+ |
| Disco | 500MB+ |
| OS | Windows, Linux, macOS |

## 🚀 Próximas Acciones

1. **Leer** `QUICKSTART.md`
2. **Ejecutar** `mvn spring-boot:run`
3. **Probar** API con ejemplos de `EXAMPLES.md`
4. **Explorar** código fuente (comienza por `domain/`)
5. **Extender** sistema (ver `CONTRIBUTING.md`)

## 📚 Referencias Recomendadas

- Arquitectura Hexagonal: https://alistair.cockburn.us/hexagonal-architecture/
- Domain-Driven Design: Eric Evans
- Clean Architecture: Robert C. Martin
- Spring Boot: https://spring.io/projects/spring-boot

---

**Última actualización**: 2026-08-12

