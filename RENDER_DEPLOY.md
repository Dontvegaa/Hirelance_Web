# 🚀 Guía de Deploy en Render

## ✅ Configuración Completada

El proyecto ya está configurado para funcionar con el puerto dinámico de Render usando la variable de entorno `PORT`.

## 📋 Configuración en Render

### 1. **Crear un nuevo Web Service**
1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en "New +" → "Web Service"
3. Conecta tu repositorio de GitHub/GitLab

### 2. **Configuración del Servicio**

#### **Build & Deploy**
- **Environment**: `Docker`
- **Region**: Elige la más cercana (US West, US East, etc.)
- **Branch**: `main` o `docker`

#### **Docker Configuration**
Render detectará automáticamente el `Dockerfile` y lo usará.

#### **Variables de Entorno** (Environment Variables)

Agrega estas variables en Render:

```plaintext
# Base de datos
SPRING_DATASOURCE_URL=jdbc:mysql://srv1999.hstgr.io/u825468745_hirelance_db
SPRING_DATASOURCE_USERNAME=u825468745_hire
SPRING_DATASOURCE_PASSWORD=Juas5378

# Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Puerto (Render lo asigna automáticamente, NO lo configures manualmente)
# PORT se asigna automáticamente por Render

# Opcional: Configuración adicional
SPRING_JPA_SHOW_SQL=false
SPRING_THYMELEAF_CACHE=true
```

⚠️ **IMPORTANTE**: NO configures la variable `PORT` manualmente. Render la asigna automáticamente.

### 3. **Plan de Render**
- **Free Tier**: Suficiente para desarrollo/pruebas
- **Starter o superior**: Para producción

### 4. **Configuración del Dockerfile para Render**

El Dockerfile ya está optimizado para Render:
- ✅ Puerto dinámico configurado
- ✅ Multi-stage build para optimizar tamaño
- ✅ Variables de entorno configuradas

## 🔧 Archivo de Configuración Alternativo (render.yaml)

Si prefieres usar Infrastructure as Code, crea este archivo en la raíz:

```yaml
services:
  - type: web
    name: hirelance
    env: docker
    region: oregon
    plan: free
    branch: docker
    dockerfilePath: ./Dockerfile
    dockerContext: .
    envVars:
      - key: SPRING_DATASOURCE_URL
        value: jdbc:mysql://srv1999.hstgr.io/u825468745_hirelance_db
      - key: SPRING_DATASOURCE_USERNAME
        value: u825468745_hire
      - key: SPRING_DATASOURCE_PASSWORD
        sync: false  # Marca como secreto
      - key: SPRING_JPA_HIBERNATE_DDL_AUTO
        value: update
      - key: SPRING_THYMELEAF_CACHE
        value: true
      - key: SPRING_JPA_SHOW_SQL
        value: false
```

## 🌐 Cómo Funciona el Puerto

### **Configuración en application.properties:**
```properties
server.port=${PORT:${SERVER_PORT:8000}}
```

Esta configuración usa:
1. **`PORT`** - Variable de Render (prioridad 1)
2. **`SERVER_PORT`** - Variable de Docker local (prioridad 2)
3. **`8000`** - Puerto por defecto para desarrollo local (prioridad 3)

### **Comportamiento:**
- **En Render**: Usa el puerto asignado dinámicamente por Render
- **En Docker local**: Usa el puerto 8000 (configurado en docker-compose)
- **En desarrollo local**: Usa el puerto 8000 por defecto

## 📝 Checklist de Deploy

- [x] Puerto configurado con variables de entorno
- [x] Dockerfile optimizado con multi-stage build
- [x] Variables de entorno de base de datos listas
- [x] .dockerignore configurado
- [ ] Agregar variables de entorno en Render
- [ ] Configurar dominio personalizado (opcional)
- [ ] Configurar SSL/TLS (automático en Render)

## 🐛 Troubleshooting en Render

### Error: "Port already in use"
✅ Ya está solucionado. La aplicación ahora usa `${PORT}` dinámicamente.

### Error: "Application failed to start"
1. Revisa los logs en Render Dashboard
2. Verifica que todas las variables de entorno estén configuradas
3. Verifica la conexión a la base de datos

### Error: "Build failed"
1. Asegúrate de que el Dockerfile esté en la raíz del proyecto
2. Verifica que el archivo `mvnw` tenga permisos de ejecución
3. Revisa los logs de build en Render

### La aplicación inicia pero no responde
1. Verifica que `server.port=${PORT:8000}` esté en application.properties
2. Asegúrate de que no haya ningún firewall bloqueando el puerto
3. Revisa que EXPOSE en el Dockerfile sea correcto

## 🔐 Seguridad

### Variables de entorno sensibles:
- Usa "Environment Variables" en Render, no las pongas en el código
- Marca las passwords como secretas en Render
- No hagas commit de archivos con credenciales

### Recomendaciones:
1. Cambia las credenciales de la base de datos en producción
2. Usa HTTPS (Render lo proporciona automáticamente)
3. Configura CORS si necesitas acceso desde otros dominios
4. Considera usar Render's Private Networking para la base de datos

## 📊 Monitoreo

### En Render puedes ver:
- **Logs**: En tiempo real desde el dashboard
- **Metrics**: CPU, memoria, requests
- **Health checks**: Configura endpoints de salud

### Configurar Health Check en Render:
```
Health Check Path: /actuator/health
```

Para habilitar actuator, agrega a tu `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 🚀 Deploy Automático

Render hace deploy automático cuando:
- Haces push a la rama configurada (main/docker)
- Cambias variables de entorno
- Manualmente desde el dashboard

## 📞 Comandos Útiles

### Ver logs en tiempo real:
```bash
# Desde Render CLI (si está instalado)
render logs -f
```

### Trigger manual deploy:
Desde el Dashboard → "Manual Deploy" → "Deploy latest commit"

## 🎯 URL de tu aplicación

Después del deploy, Render te dará una URL como:
```
https://hirelance.onrender.com
```

Puedes configurar un dominio personalizado desde:
Dashboard → Settings → Custom Domains

---

## ✨ Resumen de Cambios Realizados

1. ✅ **Puerto dinámico**: `server.port=${PORT:${SERVER_PORT:8000}}`
2. ✅ **Dockerfile optimizado**: Multi-stage build
3. ✅ **Variables de entorno**: Configuradas para Render
4. ✅ **Compatibilidad**: Funciona en local y Render

**¡Tu aplicación está lista para deploy en Render!** 🎉

---

**Última actualización**: 2025-11-14
