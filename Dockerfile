# =====================================================================
# DOCKERFILE DE PRODUCCIÓN — ROLICODE (Java 21 + Spring Boot 4)
# =====================================================================
# Etapa 1: Compilar el proyecto con Maven y Java 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Ejecutar el servidor ligero en producción
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
