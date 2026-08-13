# Guía: Flyway + Docker Compose Simplificado

## ✅ Lo que cambié

1. **Flyway integrado** — Migraciones SQL automáticas en lugar de Hibernate `ddl-auto`.
   - Archivo: `src/main/resources/db/migration/V1__init.sql`
   - Crea tabla `tasks` con índices automáticamente al iniciar.

2. **Docker Compose simplificado** — Solo usa variable `SPRING_PROFILES_ACTIVE: postgresql`.
   - Toda la configuración viene de `application-postgresql.yml`.
   - Sin necesidad de variables de entorno complejas en docker-compose.yml.

3. **Application-postgresql.yml** — Configuración clara y separada.
   - Datasource hardcodeada correcta (host: `postgres` en Docker).
   - Flyway habilitado.
   - `ddl-auto: validate` (Flyway maneja CREATE/UPDATE).

4. **Application.yml** — H2 por defecto (desarrollo local).
   - Flyway también habilitado para H2.
   - Puerto: 8081.

##  Pasos para Levantar

### Opción A: Con Docker Compose (Postgres + App)

```powershell
cd C:\Users\Ricardo\Documents\sync_task

# 1. Detén y elimina contenedores viejos
docker compose down

# 2. Reconstruye imagen (por Flyway y cambios)
docker compose build --no-cache

# 3. Levanta todo
docker compose up -d

# 4. Verifica logs
docker logs -f task-processor-app

# Deberías ver:
# - Flyway: "V1__init.sql" migration applied
# - Tomcat started on port(s): 8081
```

### Opción B: H2 Local (sin Docker)

```powershell
cd C:\Users\Ricardo\Documents\sync_task

# 1. Compila
mvn clean compile

# 2. Ejecuta
mvn spring-boot:run

# 3. Verifica logs — deberías ver:
# - Flyway: "V1__init.sql" migration applied
# - Tomcat started on port(s): 8081
# - H2 console enabled: http://localhost:8081/h2-console
```

##  Verificar que la BD se creó

### Con Postgres (Docker)

```powershell
# Conecta a Postgres dentro del contenedor
docker exec -it task-processor-db psql -U taskuser -d taskdb

# Dentro de psql:
\dt
# Deberías ver tabla: tasks

SELECT * FROM tasks LIMIT 10;
# (vacío al inicio, pero estructura OK)

\q
# (salir de psql)
```

### Con H2 (Local)

- Abre: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:taskdb`
- User: `sa`
- Pass: (vacío)
- Consulta:
```sql
SELECT * FROM information_schema.tables WHERE table_name = 'TASKS';
SELECT COUNT(*) FROM tasks;
```

##  Probar el Flujo

1) Crea una tarea:
```bash
curl -s -X POST http://localhost:8081/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-flyway",
    "description": "Prueba con Flyway",
    "payload": "{\"x\": 1}",
    "priority": "HIGH"
  }' | jq .
```

2) Revisa logs de la app (deberías ver procesamiento):
```powershell
docker logs -f task-processor-app
# O en la terminal donde ejecutaste `mvn spring-boot:run`
```

3) Consulta la BD para ver el estado:
```powershell
docker exec -it task-processor-db psql -U taskuser -d taskdb
# SELECT id, status, retry_count, created_at, updated_at FROM tasks ORDER BY created_at DESC;
```

##  Archivos Modificados

- `pom.xml` — Agregado Flyway 10.2.0
- `src/main/resources/db/migration/V1__init.sql` — Nueva migración
- `src/main/resources/application.yml` — Simplificado para H2 + Flyway
- `src/main/resources/application-postgresql.yml` — Simplificado, sin vars de entorno
- `docker-compose.yml` — Reducidas variables, solo `SPRING_PROFILES_ACTIVE`

##  Si hay problemas

### "relation 'tasks' does not exist"
- Flyway no ejecutó la migración.
- Verifica: `docker logs task-processor-app` contiene "Flyway" + "V1__init.sql"?
- Si no, reinicia: `docker compose restart task-processor-app`

### "invalid value for parameter 'loglevel'"
- Es un warning de Postgres, no afecta. Ignora.

### "ddl-auto validation failed"
- Significa que Hibernate validó pero Flyway no había creado la tabla.
- Asegúrate que Flyway ejecutó primero (check logs).

## ✨ Beneficios de Flyway

✅ Migraciones versionadas (V1, V2, V3...).  
✅ Control de cambios en Git.  
✅ Reproducible en cualquier BD.  
✅ No depende de `ddl-auto` (que es impredecible).  
✅ Fácil agregar nuevas migraciones.  

##  Próximas Migraciones

Para agregar una nueva tabla o columna:
1. Crea: `src/main/resources/db/migration/V2__add_users_table.sql`
2. Escribe el SQL
3. Redeploy — Flyway la ejecutará automáticamente en orden.

¡Listo! Docker ahora solo usa `application-postgresql.yml` y Flyway maneja todo.
