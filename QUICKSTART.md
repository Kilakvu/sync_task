# Quick Start Guide

## Requisitos Previos
- Java 21 o superior
- Maven 3.9 o superior
- Git

## Instalación Rápida

### 1. Clonar/Descargar el proyecto
```bash
cd sync_task
```

### 2. Compilar el proyecto
```bash
mvn clean compile
```

### 3. Ejecutar la aplicación

#### Opción A: Con Maven (Recomendado para desarrollo)
```bash
mvn spring-boot:run
```

#### Opción B: Con JAR ejecutable
```bash
# Primero compilar
mvn clean package -DskipTests

# Luego ejecutar
java -jar target/sync_task-1.0-SNAPSHOT.jar
```

#### Opción C: Con Docker
```bash
# Compilar imagen Docker
docker build -t sync-task-system .

# Ejecutar contenedor
docker run -p 8080:8080 sync-task-system
```

#### Opción D: Con Docker Compose (con PostgreSQL)
```bash
docker-compose up
```

## Verificar que está funcionando

Una vez que la aplicación esté corriendo, verifica:

```bash
curl http://localhost:8080/api/v1/tasks
```

Deberías recibir un JSON vacío: `[]`

## Crear tu primer tarea

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mi Primera Tarea",
    "description": "Prueba del sistema",
    "payload": "{\"test\": true}",
    "priority": "HIGH"
  }'
```

## Acceso a H2 Console

Mientras la aplicación está corriendo, accede a:
```
http://localhost:8080/h2-console
```

- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (dejar en blanco)

## Documentación Disponible

- **README.md**: Información general del proyecto
- **ARCHITECTURE.md**: Detalles de arquitectura hexagonal
- **EXAMPLES.md**: Ejemplos de uso de la API
- **CONTRIBUTING.md**: Cómo contribuir al proyecto

## Estructura del Proyecto

```
sync_task/
├── README.md                 # Información del proyecto
├── ARCHITECTURE.md           # Guía de arquitectura
├── EXAMPLES.md              # Ejemplos de uso
├── CONTRIBUTING.md          # Guía de contribución
├── pom.xml                  # Configuración Maven
├── Dockerfile               # Para ejecutar en Docker
├── docker-compose.yml       # Para ejecutar con dependencias
└── src/
    ├── main/java/org/dataki/
    │   ├── domain/          # Lógica de negocio
    │   ├── application/     # Casos de uso
    │   ├── adapters/        # Controllers REST
    │   └── infrastructure/  # Implementaciones técnicas
    └── main/resources/
        └── application.yml  # Configuración Spring
```

## Solución de Problemas

### Error: "JAVA_HOME not configured"
```bash
# En Windows
set JAVA_HOME=C:\Program Files\Java\jdk-21
mvn spring-boot:run

# En Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-21
mvn spring-boot:run
```

### Puerto 8080 ya en uso
```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

### Error de compilación con Lombok
Instala el plugin de Lombok en tu IDE:
- IntelliJ: File → Settings → Plugins → Buscar "Lombok"
- VS Code: Instala "Lombok Annotations Support"

## Próximos Pasos

1. **Explorar la API**: Ver EXAMPLES.md
2. **Entender la arquitectura**: Ver ARCHITECTURE.md
3. **Crear tests**: Ver CONTRIBUTING.md
4. **Extender la funcionalidad**: Agregar nuevos adaptadores

## Contacto y Soporte

- Revisa los Issues en GitHub
- Consulta la documentación en los .md files
- Crea una discusión para preguntas generales

¡Que disfrutes desarrollando! 🚀

