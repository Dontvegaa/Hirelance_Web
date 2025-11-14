# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen de Java 17 (JDK) para compilar tu código
FROM eclipse-temurin:17-jdk-jammy AS builder

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de Maven para descargar dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Damos permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargamos las dependencias (esto se guarda en caché)
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código fuente
COPY src/ ./src

# Compilamos el proyecto y creamos el .war (saltamos los tests)
# Esto creará tu "Hirelance-0.0.1-SNAPSHOT.war"
RUN ./mvnw package -DskipTests


# --- ETAPA 2: Ejecución (Run) ---
# Usamos una imagen de Java 17 (JRE) mucho más ligera para correr la app
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copiamos solo el .war construido desde la etapa 'builder'
# Usamos un comodín (*.war) para que coincida con "Hirelance-0.0.1-SNAPSHOT.war"
COPY --from=builder /app/target/*.war app.war

# Exponemos el puerto estándar 8000 de Spring Boot
EXPOSE 8000

# El comando que se ejecutará cuando inicie el contenedor
# (Spring Boot permite ejecutar .war empaquetados de esta forma)
ENTRYPOINT ["java", "-jar", "app.war"]