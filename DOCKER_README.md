# Docker Setup - Hirelance

## ✅ Configuración Completada

Este proyecto está completamente configurado para ejecutarse con Docker siguiendo las mejores prácticas.

## 📋 Archivos Docker

### 1. **Dockerfile** (Multi-stage build)
- **Etapa 1 (Builder)**: Compila la aplicación usando Maven
  - Imagen base: `eclipse-temurin:17-jdk-jammy`
  - Optimización con cache de dependencias
  - Genera el archivo WAR
  
- **Etapa 2 (Runtime)**: Ejecuta la aplicación
  - Imagen base: `eclipse-temurin:17-jre-jammy` (más ligera)
  - Solo contiene el WAR compilado
  - Reduce el tamaño final de la imagen

### 2. **docker-compose.yml**
- Define el servicio de la aplicación
- Mapeo de puertos: `8080:8000` (host:container)
- Variables de entorno para la base de datos externa
- Volúmenes para persistir archivos subidos

### 3. **.dockerignore**
- Excluye archivos innecesarios del build
- Optimiza el tiempo de construcción
- Reduce el tamaño de la imagen

## 🚀 Comandos Docker

### Construir y ejecutar
```bash
sudo docker compose up --build -d
```

### Ver el estado de los contenedores
```bash
sudo docker compose ps
```

### Ver logs en tiempo real
```bash
sudo docker compose logs -f app
```

### Ver últimos N logs
```bash
sudo docker compose logs --tail=50 app
```

### Detener la aplicación
```bash
sudo docker compose down
```

### Reiniciar la aplicación
```bash
sudo docker compose restart
```

### Reconstruir sin cache
```bash
sudo docker compose build --no-cache
sudo docker compose up -d
```

## 🌐 Acceso a la Aplicación

- **URL Local**: http://localhost:8080
- **Puerto del contenedor**: 8000
- **Puerto del host**: 8080

## 📦 Características Docker Implementadas

✅ **Multi-stage build** - Optimiza el tamaño de la imagen  
✅ **.dockerignore** - Excluye archivos innecesarios  
✅ **Variables de entorno** - Configuración flexible  
✅ **Volúmenes** - Persistencia de datos  
✅ **Health checks** - Monitoreo automático  
✅ **Restart policy** - Reinicio automático en caso de fallo  
✅ **Cache de dependencias** - Builds más rápidos  

## 🔧 Configuración

### Base de Datos
La aplicación se conecta a una base de datos MySQL externa:
- **Host**: srv1999.hstgr.io
- **Base de datos**: u825468745_hirelance_db
- **Puerto de la aplicación**: 8000 (interno)

### Volúmenes Persistentes
```yaml
- ./uploads:/app/uploads
- ./downloads:/app/downloads
```

## 📊 Información Técnica

- **Java Version**: 17
- **Spring Boot**: 3.5.7
- **Imagen Base**: Eclipse Temurin
- **Build Tool**: Maven
- **Package**: WAR

## 🐛 Troubleshooting

### Si el contenedor no inicia:
```bash
sudo docker compose logs app
```

### Si hay problemas de permisos:
```bash
sudo chmod +x mvnw
```

### Limpiar todo y empezar de nuevo:
```bash
sudo docker compose down -v
sudo docker system prune -a
sudo docker compose up --build -d
```

## 📝 Notas

- El archivo `application.properties` fue corregido para usar codificación UTF-8 sin BOM
- El puerto 8000 es el puerto interno del contenedor
- El mapeo de puertos permite acceder desde el puerto 8080 del host
- Los archivos subidos se persisten en volúmenes locales

## ✨ Best Practices Implementadas

1. **Separación de etapas**: Build y runtime separados
2. **Imágenes oficiales**: Eclipse Temurin (OpenJDK oficial)
3. **Cache de capas**: Dependencias se cachean para builds más rápidos
4. **Permisos correctos**: chmod +x para mvnw
5. **Variables de entorno**: Configuración externalizada
6. **Volúmenes**: Datos persistentes fuera del contenedor
7. **Restart policy**: Alta disponibilidad
8. **Logs estructurados**: Fácil debugging

---

**Proyecto**: Hirelance - Plataforma de Freelancing  
**Última actualización**: 2025-11-14
