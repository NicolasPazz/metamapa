# Dockerfile generico para servicios Spring Boot
# Se usa con build args para especificar el modulo a construir

FROM maven:3.9-eclipse-temurin-17 AS build

# Argumentos de construccion
ARG MODULE_PATH
ARG MODULE_NAME

WORKDIR /app

# Copiar archivos de configuracion de Maven
COPY pom.xml .

# Copiar POMs del backend y frontend
COPY backend/pom.xml ./backend/
COPY frontend/pom.xml ./frontend/

# Copiar POMs de todos los modulos del backend
COPY backend/agregador/pom.xml ./backend/agregador/
COPY backend/dinamica/pom.xml ./backend/dinamica/
COPY backend/estatica/pom.xml ./backend/estatica/
COPY backend/estadistica/pom.xml ./backend/estadistica/
COPY backend/proxy_ong/pom.xml ./backend/proxy_ong/
COPY backend/proxy_metamapa/pom.xml ./backend/proxy_metamapa/
COPY backend/users/pom.xml ./backend/users/

# Descargar dependencias (cache layer)
RUN mvn dependency:go-offline -B

# Copiar codigo fuente del modulo especifico
COPY ${MODULE_PATH}/src ./${MODULE_PATH}/src

# Construir la aplicacion
RUN mvn clean package -DskipTests -pl ${MODULE_PATH} -am

# Imagen de runtime
FROM eclipse-temurin:17-jre-alpine

# Instalar wget para health checks
RUN apk add --no-cache wget

WORKDIR /app

# Copiar el JAR construido
ARG MODULE_PATH
ARG MODULE_NAME
COPY --from=build /app/${MODULE_PATH}/target/*.jar app.jar

# Exponer puerto (se sobrescribe en docker-compose)
EXPOSE 8080

# Health check (se puede sobrescribir en docker-compose)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health 2>/dev/null || wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/ 2>/dev/null || exit 1

# Ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]

