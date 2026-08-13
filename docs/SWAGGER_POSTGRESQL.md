# Guía de Swagger y PostgreSQL

##  API Documentation con Swagger/OpenAPI

La API está completamente documentada con **Swagger/OpenAPI 3.0** usando Spring Doc.

###  Acceder a Swagger UI

Una vez que la aplicación esté corriendo:

```
http://localhost:8081/swagger-ui.html
```

###  Swagger API Docs (JSON)

```
http://localhost:8081/v3/api-docs
```

###  Características de Swagger

✅ **Documentación interactiva** con ejemplos  
✅ **Try it out**: Probar endpoints directamente desde el navegador  
✅ **Schemas**: Ver estructura de requests/responses  
✅ **Respuestas**: Códigos HTTP y descripciones  
✅ **Parámetros**: Documentación de cada parámetro  

###  Ejemplo: Crear Tarea desde Swagger

1. Abre http://localhost:8081/swagger-ui.html
2. Expande **POST /api/v1/tasks**
3. Haz clic en **"Try it out"**
4. Rellena el JSON:
```json
{
  "name": "Mi Primera Tarea",
  "description": "Tarea de prueba",
  "payload": "{\"test\": true}",
  "priority": "HIGH",
  "maxRetries": 3
}
```
5. Haz clic en **"Execute"**
6. Ver respuesta en **Responses**

---

##  PostgreSQL Setup

### Opción 1: Con Docker Compose (Recomendado)

```bash
docker-compose up
```

Esto inicia:
- ✅ PostgreSQL en puerto 5432
- ✅ Aplicación en puerto 8081
- ✅ pgAdmin en puerto 5050

### Opción 2: PostgreSQL Manual

#### Instalar PostgreSQL
```bash
# Windows (scoop)
scoop install postgresql

# Las instrucciones dependen de tu SO
```

#### Crear Base de Datos
```sql
CREATE DATABASE taskdb;
CREATE USER taskuser WITH ENCRYPTED PASSWORD 'taskpass123';
GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;
```

#### Activar Perfil PostgreSQL
```bash
# Con Maven
export SPRING_PROFILES_ACTIVE=postgresql
mvn spring-boot:run

# Con variables de entorno (Windows)
set SPRING_PROFILES_ACTIVE=postgresql
mvn spring-boot:run
```

### Opción 3: Cambiar en application.yml

```yaml
spring:
  profiles:
    active: postgresql
```

---

##  Cambiar Entre H2 y PostgreSQL

### Usar H2 (Development)
```bash
# Configuración por defecto
mvn spring-boot:run
# O accesa: http://localhost:8081/h2-console
```

### Usar PostgreSQL (Production)
```bash
# Con perfil
export SPRING_PROFILES_ACTIVE=postgresql
mvn spring-boot:run

# O con Docker
docker-compose up
```

---

## ️ Conectar a PostgreSQL con pgAdmin

1. Abre http://localhost:5050 (si usas Docker Compose)
2. Login:
   - Email: `admin@example.com`
   - Contraseña: `admin`
3. **Registrar Servidor**:
   - Nombre: `Task Database`
   - Host: `postgres` (si está en Docker) o `localhost` (local)
   - Puerto: `5432`
   - Usuario: `taskuser`
   - Contraseña: `taskpass123`
4. Verifica la base de datos y tablas

---

##  Ver Datos en PostgreSQL

### Con psql (línea de comandos)
```bash
psql -h localhost -U taskuser -d taskdb

# Ver tablas
\dt

# Ver estructura de tasks
\d tasks

# Query
SELECT * FROM tasks;
```

### Con pgAdmin (GUI)
- Navega a `taskdb` → `Schemas` → `public` → `Tables` → `tasks`
- Haz clic derecho → **View/Edit Data** → **Top 100 Rows**

---

##  Configuración de Perfiles Spring

### application.yml (H2 - Desarrollo)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:taskdb
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### application-postgresql.yml (PostgreSQL - Producción)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: taskuser
    password: taskpass123
  jpa:
    hibernate:
      ddl-auto: update
```

¿Cómo cambiar?
```bash
# Activar perfil PostgreSQL
export SPRING_PROFILES_ACTIVE=postgresql
mvn spring-boot:run

# O en Docker
docker-compose up
```

---

##  Endpoints Documentados

Todos los endpoints están documentados en Swagger con:

✅ **Descripción** clara  
✅ **Parámetros** documentados  
✅ **Respuestas** con ejemplos  
✅ **Códigos HTTP** (200, 201, 400, 404, 500)  
✅ **Modelos** con tipos de datos  

### Ejemplos de Endpoints

```
POST   /api/v1/tasks                 Crear tarea
GET    /api/v1/tasks                 Listar todas
GET    /api/v1/tasks/{id}            Obtener por ID
GET    /api/v1/tasks/status/{status} Filtrar por estado
GET    /api/v1/tasks/queue/pending   Ver cola
```

Todos visibles en: http://localhost:8081/swagger-ui.html

---

##  Solución de Problemas

### Error: Connection refused to PostgreSQL
```
Solución: Verifica que PostgreSQL está corriendo
docker ps  # si usas Docker
psql -U taskuser -d taskdb  # para conectar localmente
```

### Error: Database taskdb does not exist
```sql
CREATE DATABASE taskdb;
```

### No aparecen datos en PostgreSQL
```bash
# Reinicia con ddl-auto: create
# application-postgresql.yml:
spring:
  jpa:
    hibernate:
      ddl-auto: create  # en lugar de update
```

### Swagger no aparece
```
Verifica que está en:
http://localhost:8081/swagger-ui.html

No en:
http://localhost:8081/swagger-ui/
```

---

##  Flujo Recomendado

### Desarrollo Local
```bash
# 1. H2 en memoria (rápido, sin dependencias)
mvn spring-boot:run

# 2. Accede a Swagger
# http://localhost:8081/swagger-ui.html

# 3. Prueba endpoints
# Haz clic en "Try it out"
```

### Testing/Staging
```bash
# 1. PostgreSQL en Docker
docker-compose up

# 2. Accede a Swagger
# http://localhost:8081/swagger-ui.html

# 3. Accede a pgAdmin
# http://localhost:5050

# 4. Verifica datos en DB
```

### Producción
```bash
# 1. PostgreSQL en servidor remoto
# 2. Configurar en environment variables
# 3. Ejecutar aplicación
# 4. Swagger disponible para debugging
```

---

##  Referencias

- **Swagger/OpenAPI**: https://swagger.io/
- **SpringDoc**: https://springdoc.org/
- **PostgreSQL**: https://www.postgresql.org/
- **pgAdmin**: https://www.pgadmin.org/

---

**Última actualización**: 2026-08-13
