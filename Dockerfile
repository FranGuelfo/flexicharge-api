# Usamos una imagen ligera de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR generado por Maven
# Nota: Asegúrate de hacer 'mvn clean package' antes de construir la imagen
COPY target/flexicharge-*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]