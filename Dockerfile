# Usar maven como imagen base para compilar
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml e instalar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar código fuente
COPY src src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Usar Java 21 como imagen base para runtime
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copiar JAR compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Crear usuario no-root
RUN useradd -m -u 1001 appuser && chown -R appuser:appuser /app
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
