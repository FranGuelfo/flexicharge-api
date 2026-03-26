# 1. Imagen base oficial de Java 17 (Ligera y segura)
FROM eclipse-temurin:17-jdk-alpine

# 2. Crear un usuario de sistema (Seguridad Pro)
# No es recomendable ejecutar apps como 'root' dentro de Docker
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 3. Directorio de trabajo
WORKDIR /app

# 4. Copiamos el JAR generado
# Usamos un argumento para que sea flexible si cambia la versión en el pom.xml
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 5. Optimizamos el arranque de Java
# 'TieredCompilation' ayuda a que Spring Boot arranque más rápido en contenedores
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]

# 6. Exponemos el puerto de tu app
EXPOSE 8080