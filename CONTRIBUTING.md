# Guía de Contribución

## Cómo Contribuir

¡Gracias por tu interés en contribuir a este proyecto! Este documento te guiará sobre cómo contribuir de manera efectiva.

## Código de Conducta

Por favor, sé respetuoso con otros contribuyentes y usuarios. Cualquier tipo de acoso, discriminación o comportamiento inapropiado no será tolerado.

## Reportar Bugs

### Antes de reportar
- Verifica que el bug no haya sido reportado ya
- Verifica que estás usando la última versión
- Recopila información sobre tu entorno (OS, Java version, etc.)

### Cómo reportar
1. Usa un título descriptivo
2. Describe el problema paso a paso
3. Proporciona ejemplos concretos
4. Incluye logs o stacktraces si aplica
5. Describe el comportamiento esperado vs actual

### Ejemplo de Bug Report
```
Título: TaskWorker no retira tareas completadas de la cola

Descripción:
Cuando una tarea se marca como COMPLETED, el TaskWorker sigue 
procesándola en cada iteración.

Pasos para reproducir:
1. Crear una tarea (POST /api/v1/tasks)
2. Esperar que se procese (status = COMPLETED)
3. Ejecutar GET /api/v1/tasks/queue/pending
4. Ver que la tarea completada aparece aún en la cola

Esperado: Las tareas completadas no deben estar en la cola de pendientes

Comportamiento actual: Aparecen tareas completadas en la cola

Ambiente:
- OS: Windows 11
- Java: 21.0.9
- Spring Boot: 3.2.0
```

## Solicitar Features

### Antes de solicitar
- Verifica que la feature no haya sido solicitada ya
- Asegúrate que es útil para la mayoría de usuarios
- Considera si se alinea con la visión del proyecto

### Cómo solicitar
1. Usa un título claro y descriptivo
2. Proporciona una descripción detallada del caso de uso
3. Ejemplos de cómo se usaría la feature
4. Beneficios de implementarla

### Ejemplo de Feature Request
```
Título: Agregar soporte para Webhooks al completar tareas

Descripción:
Permitir que los usuarios registren webhooks que se ejecuten
cuando una tarea se completa o falla.

Caso de uso:
Un usuario necesita notificar a su sistema cuando un reporte 
se genera exitosamente.

Ejemplo de uso:
POST /api/v1/webhooks
{
  "event": "TASK_COMPLETED",
  "url": "https://api.example.com/callbacks/task",
  "retries": 3
}

Beneficios:
- Integración más fácil con sistemas externos
- Mejor notificación de eventos
- Reduce necesidad de polling
```

## Proceso de Contribución de Código

### 1. Fork del Repositorio
```bash
git clone https://github.com/tu-usuario/sync_task.git
cd sync_task
```

### 2. Crear una Rama
```bash
git checkout -b feature/descripcion-feature
# o para bugs
git checkout -b bugfix/descripcion-bug
```

Convención de nombres:
- Features: `feature/nombre-descriptivo`
- Bugs: `bugfix/nombre-descriptivo`
- Hotfixes: `hotfix/nombre-descriptivo`
- Mejoras: `improvement/nombre-descriptivo`

### 3. Hacer los Cambios

#### Estilo de Código
- Use nombres claros y descriptivos
- Documente métodos públicos con JavaDoc
- Límite de 120 caracteres por línea
- 4 espacios de indentación (no tabs)

#### Estructura de Commits
```bash
# Mensaje de commit descriptivo
git commit -m "feat: Agregar soporte para consultas gráficas"
git commit -m "fix: Corregir race condition en TaskWorker"
git commit -m "docs: Actualizar README con ejemplos"
```

Prefijos de commit:
- `feat:` - Nueva feature
- `fix:` - Bug fix
- `docs:` - Documentación
- `style:` - Cambios de formato
- `refactor:` - Refactorización sin cambios de funcionalidad
- `perf:` - Mejoras de performance
- `test:` - Agregar o actualizar tests

### 4. Escribir Tests

Toda nueva functionality DEBE incluir tests:

```java
@Test
public void shouldCreateTaskWithValidData() {
    // Arrange
    CreateTaskRequest request = new CreateTaskRequest(
        "Test Task",
        "Test Description",
        "{\"key\": \"value\"}",
        TaskPriority.HIGH,
        3,
        null
    );
    
    // Act
    Task result = createTaskService.createTask(
        request.name(),
        request.description(),
        request.payload(),
        request.priority(),
        request.maxRetries(),
        request.scheduledFor()
    );
    
    // Assert
    assertNotNull(result.getId());
    assertEquals("Test Task", result.getName());
    assertEquals(TaskStatus.PENDING, result.getStatus());
}
```

### 5. Ejecutar Tests Localmente
```bash
mvn clean test
```

### 6. Hacer Push a tu Rama
```bash
git push origin feature/descripcion-feature
```

### 7. Crear un Pull Request

En GitHub:
1. Ve a "Compare & pull request"
2. Llena el template del PR
3. Describe qué cambios hace
4. Referencia cualquier issue relacionado (#123)
5. Asegúrate que los tests pasan

#### Template de Pull Request
```markdown
## Descripción
Breve descripción de los cambios

## Tipo de Cambio
- [ ] Bug fix
- [ ] Nueva feature
- [ ] Breaking change
- [ ] Documentación

## Cambios Realizados
- Punto 1
- Punto 2
- Punto 3

## Testing Realizado
- Cómo probaste los cambios
- Test cases relevantes

## Checklist
- [ ] He seguido el estilo de código del proyecto
- [ ] He actualizado la documentación si es necesario
- [ ] He agregado pruebas para mis cambios
- [ ] Las pruebas nuevas y existentes pasan
- [ ] No hay cambios no solicitados

## Screenshots (si aplica)
```

## Directrices de Calidad

### Code Review Checklist
- El código es legible y fácil de entender
- Sigue el estilo del proyecto
- No tiene code smells
- Los tests son adecuados
- La documentación está actualizada
- No introduce dependencias innecesarias
- El rendimiento es aceptable

### Requisitos Mínimos para PR
- Mínimo 80% de cobertura de tests
- Todos los tests pasan
- Sin warnings de compilación
- Documentación actualizada
- Code review de al menos 1 contribuyente

## Configuración de Desarrollo

### IDE Setup (IntelliJ IDEA)
1. Abre el proyecto en IntelliJ
2. Ve a File → Project Structure → Project
3. Asegúrate que el SDK es Java 21
4. Instala el plugin Lombok
5. Habilita "Annotation Processing"

### IDE Setup (VS Code)
1. Instala Extension Pack for Java
2. Instala Lombok Annotations Support
3. Asegúrate que tienes Java 21 en PATH

### Configuración de Pre-commit
```bash
# Para validar antes de commit
npm install -g husky
husky install
```

## Estructura de Carpetas

```
src/
├── main/
│   ├── java/org/dataki/
│   │   ├── domain/
│   │   ├── application/
│   │   ├── adapters/
│   │   └── infrastructure/
│   └── resources/
│       └── application.yml
└── test/
    └── java/org/dataki/
```

## Documentación

### JavaDoc
```java
/**
 * Procesa una tarea de forma asíncrona.
 *
 * @param taskId ID de la tarea a procesar
 * @throws IllegalArgumentException si el task no existe
 * @throws InterruptedException si se interrumpe el thread
 */
public void executeTask(String taskId) throws InterruptedException {
    // implementación
}
```

### Markdown
- README: Información general del proyecto
- ARCHITECTURE.md: Decisiones arquitectónicas
- EXAMPLES.md: Ejemplos de uso
- CONTRIBUTING.md: Guía de contribución

## Proceso de Release

1. Actualizar versión en pom.xml
2. Actualizar CHANGELOG
3. Crear tag en git
4. Publicar a Maven Central (opcional)

## Ayuda

- **Documentación**: Ver ARCHITECTURE.md y EXAMPLES.md
- **Issues**: Busca en GitHub issues existentes
- **Discussions**: Abre una discusión para preguntas
- **Email**: contribuciones@example.com

## Licencia

Al contribuir, aceptas que tu código será licenciado bajo MIT License.

---

¡Gracias por contribuir! 🙏

