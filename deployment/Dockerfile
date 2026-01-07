# ---------- BUILD STAGE ----------
#Usa una imagen de gradle 8.6-jdk21 con pseudónimo build.
#Multi-stage build

FROM gradle:8.6-jdk21 AS build
# Setea la aplicaci´pn como el working diréctory donde estará la aplicación dentro del contenedor.
WORKDIR /app

# Copy gradle files first (cache friendly)
#Estos comandos corren dentro del working directory creado linea 5
#Copia el gradle wrapper and lo configura primero.
COPY gradlew .
#Esto mejora el caché de docker
COPY gradle gradle
COPY settings.gradle* ./
COPY main.gradle ./

## Copy the rest of the source code
# Copia el proyecto completo del source dentro del contenedor.
COPY . .

#Inside the Linux container, gradlew doesn’t have execute permission.
#This usually happens when the repo was created/edited on Windows and the executable bit wasn’t preserved.
RUN chmod +x gradlew
# Build application (skip tests for faster image build)
# Construye el spring boot ejecutable en .jar
# bootJar crea un jar ejecutable
# -x flag omite test, teniendo en cuenta que esto se hace ddesde un entorno CI (continuos integration)
RUN ./gradlew clean bootJar -x test

# dentro del docker, la app se encontrará en: /app/applications/<module>/build/libs/*.jar

# ---------- RUNTIME STAGE ----------
# Descarga de imagen con Java 21 runtinme
FROM eclipse-temurin:21-jre
# Sets execution directory
WORKDIR /app

# Copy the generated jar from the build stage
#Lo renombra como app.jar
#Se necesita para correr el programa
COPY --from=build /app/applications/*/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run application
#Define los comandos para iniciar la app dentro de docker.
# Corre la aplicación de spring boot
ENTRYPOINT ["java", "-jar", "app.jar"]

