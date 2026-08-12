# Ejemplos de Uso - API REST de Tareas

Este archivo contiene ejemplos de cómo interactuar con la API REST del sistema de procesamiento de tareas.

## 1. Crear una Tarea Inmediata (Baja Prioridad)

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Limpiar Logs",
    "description": "Eliminar archivos de log antiguos",
    "payload": "{\"directory\": \"/var/logs\", \"daysOld\": 30}",
    "priority": "LOW",
    "maxRetries": 2
  }'
```

## 2. Crear una Tarea Alta Prioridad

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Procesar Pago",
    "description": "Procesar pago urgente de cliente VIP",
    "payload": "{\"orderId\": \"ORD-2026-001\", \"amount\": 5000.00, \"currency\": \"USD\"}",
    "priority": "HIGH",
    "maxRetries": 5
  }'
```

## 3. Crear una Tarea Crítica

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sincronizar Base de Datos",
    "description": "Sincronizar réplica con base de datos principal",
    "payload": "{\"primary\": \"db-primary.internal\", \"replica\": \"db-replica.internal\"}",
    "priority": "CRITICAL",
    "maxRetries": 10
  }'
```

## 4. Crear una Tarea Programada para Más Tarde

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Generar Reporte Diario",
    "description": "Reporte de ventas del día",
    "payload": "{\"type\": \"sales\", \"date\": \"2026-08-12\"}",
    "priority": "MEDIUM",
    "maxRetries": 3,
    "scheduledFor": "2026-08-13T02:00:00"
  }'
```

## 5. Crear una Tarea Programada para Backup

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Backup Completo de BD",
    "description": "Backup completo de base de datos principal",
    "payload": "{\"database\": \"production\", \"type\": \"full\", \"compression\": true}",
    "priority": "CRITICAL",
    "maxRetries": 5,
    "scheduledFor": "2026-08-13T03:00:00"
  }'
```

## 6. Crear una Tarea de Envío de Email

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enviar Newsletter",
    "description": "Enviar contenido a suscriptores",
    "payload": "{\"templateId\": \"newsletter-001\", \"recipients\": 5000, \"subject\": \"August Newsletter\"}",
    "priority": "MEDIUM",
    "maxRetries": 3
  }'
```

## 7. Obtener una Tarea Específica

```bash
# Reemplazar {id} con el ID de la tarea devuelto en la respuesta anterior
curl http://localhost:8080/api/v1/tasks/550e8400-e29b-41d4-a716-446655440000
```

## 8. Listar Todas las Tareas

```bash
curl http://localhost:8080/api/v1/tasks
```

## 9. Obtener Tareas Pendientes

```bash
curl http://localhost:8080/api/v1/tasks/status/PENDING
```

## 10. Obtener Tareas Completadas

```bash
curl http://localhost:8080/api/v1/tasks/status/COMPLETED
```

## 11. Obtener Tareas Fallidas

```bash
curl http://localhost:8080/api/v1/tasks/status/FAILED
```

## 12. Obtener Tareas Programadas

```bash
curl http://localhost:8080/api/v1/tasks/status/SCHEDULED
```

## 13. Obtener Tareas en Reintento

```bash
curl http://localhost:8080/api/v1/tasks/status/RETRY
```

## 14. Ver Cola de Procesamiento

```bash
curl http://localhost:8080/api/v1/tasks/queue/pending
```

## Respuestas Esperadas

### Respuesta de creación exitosa (201 Created)

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "Procesar Pago",
  "description": "Procesar pago urgente de cliente VIP",
  "payload": "{\"orderId\": \"ORD-2026-001\", \"amount\": 5000.00, \"currency\": \"USD\"}",
  "status": "PENDING",
  "priority": "HIGH",
  "retryCount": 0,
  "maxRetries": 5,
  "errorMessage": null,
  "createdAt": "2026-08-12T23:15:30.123456",
  "updatedAt": "2026-08-12T23:15:30.123456",
  "scheduledFor": null,
  "startedAt": null,
  "completedAt": null
}
```

### Respuesta de tarea completada

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "Procesar Pago",
  "description": "Procesar pago urgente de cliente VIP",
  "payload": "{\"orderId\": \"ORD-2026-001\", \"amount\": 5000.00, \"currency\": \"USD\"}",
  "status": "COMPLETED",
  "priority": "HIGH",
  "retryCount": 0,
  "maxRetries": 5,
  "errorMessage": null,
  "createdAt": "2026-08-12T23:15:30.123456",
  "updatedAt": "2026-08-12T23:16:45.789123",
  "scheduledFor": null,
  "startedAt": "2026-08-12T23:16:00.456789",
  "completedAt": "2026-08-12T23:16:45.789123"
}
```

### Respuesta de tarea fallida con reintento pendiente

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "Procesar Pago",
  "description": "Procesar pago urgente de cliente VIP",
  "payload": "{\"orderId\": \"ORD-2026-001\", \"amount\": 5000.00, \"currency\": \"USD\"}",
  "status": "RETRY",
  "priority": "HIGH",
  "retryCount": 1,
  "maxRetries": 5,
  "errorMessage": "Connection timeout: Unable to reach payment gateway",
  "createdAt": "2026-08-12T23:15:30.123456",
  "updatedAt": "2026-08-12T23:16:45.789123",
  "scheduledFor": null,
  "startedAt": "2026-08-12T23:16:00.456789",
  "completedAt": null
}
```

### Respuesta de tarea programada

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "Generar Reporte Diario",
  "description": "Reporte de ventas del día",
  "payload": "{\"type\": \"sales\", \"date\": \"2026-08-12\"}",
  "status": "SCHEDULED",
  "priority": "MEDIUM",
  "retryCount": 0,
  "maxRetries": 3,
  "errorMessage": null,
  "createdAt": "2026-08-12T23:15:30.123456",
  "updatedAt": "2026-08-12T23:15:30.123456",
  "scheduledFor": "2026-08-13T02:00:00",
  "startedAt": null,
  "completedAt": null
}
```

## Usando PowerShell en Windows

```powershell
# Crear una tarea
$body = @{
    name = "Procesar Pago"
    description = "Procesar pago urgente"
    payload = '{"orderId": "ORD-2026-001", "amount": 5000.00}'
    priority = "HIGH"
    maxRetries = 5
} | ConvertTo-Json

Invoke-WebRequest -Method POST `
  -Uri http://localhost:8080/api/v1/tasks `
  -ContentType "application/json" `
  -Body $body

# Obtener todas las tareas
Invoke-WebRequest -Uri http://localhost:8080/api/v1/tasks
```

## Usando jq para Parsear Respuestas

```bash
# Obtener solo los IDs de tareas
curl http://localhost:8080/api/v1/tasks | jq '.[] | .id'

# Obtener tareas con estado específico y sus errores
curl http://localhost:8080/api/v1/tasks/status/FAILED | jq '.[] | {id: .id, error: .errorMessage}'

# Contar tareas por estado
curl http://localhost:8080/api/v1/tasks/status/COMPLETED | jq 'length'
```

## Secuencia de Prueba Completa

```bash
#!/bin/bash

echo "1. Crear tarea..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Task",
    "payload": "{\"test\": true}",
    "priority": "MEDIUM"
  }')

TASK_ID=$(echo $RESPONSE | jq -r '.id')
echo "Task ID: $TASK_ID"

sleep 2

echo "2. Consultar tarea..."
curl -s http://localhost:8080/api/v1/tasks/$TASK_ID | jq '.'

echo "3. Listar todas las tareas..."
curl -s http://localhost:8080/api/v1/tasks | jq '.[] | {id: .id, name: .name, status: .status}'

echo "4. Ver cola de procesamiento..."
curl -s http://localhost:8080/api/v1/tasks/queue/pending | jq '.[] | {id: .id, name: .name}'
```

## Notas

- El payloadse puede enviar como string JSON o como objeto directo
- Los campos priority y scheduledFor son opcionales
- maxRetries por defecto es 3 si no se especifica
- Un task creado sin scheduledFor se procesa inmediatamente
- Las tareas se procesan de acuerdo a su prioridad

