CREATE DATABASE hirelance_db;
USE hirelance_db;

-- ===========================
-- USUARIOS
-- ===========================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(120) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    tipo ENUM('estudiante','cliente','admin') DEFAULT 'estudiante',
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('activo','inactivo','baneado') DEFAULT 'activo'
);

-- ===========================
-- PERFIL DE ESTUDIANTE
-- ===========================
CREATE TABLE perfil_estudiante (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    universidad VARCHAR(150),
    carrera VARCHAR(150),
    anio_carrera INT,
    habilidades TEXT,
    descripcion TEXT,
    portafolio_url VARCHAR(255),
    foto_perfil VARCHAR(255),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- PERFIL DE CLIENTE
-- ===========================
CREATE TABLE perfil_cliente (
    id_perfil INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    empresa VARCHAR(150),
    ubicacion VARCHAR(150),
    descripcion TEXT,
    sitio_web VARCHAR(255),
    logo_empresa VARCHAR(255),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- CATEGORÍAS DE SERVICIOS
-- ===========================
CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- ===========================
-- PROYECTOS / SERVICIOS PUBLICADOS
-- ===========================
CREATE TABLE proyectos (
    id_proyecto INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_categoria INT,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    presupuesto DECIMAL(10,2),
    fecha_publicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_limite DATE,
    estado ENUM('publicado','en_progreso','finalizado','cancelado') DEFAULT 'publicado',
    FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- ===========================
-- POSTULACIONES DE ESTUDIANTES
-- ===========================
CREATE TABLE postulaciones (
    id_postulacion INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    id_estudiante INT NOT NULL,
    propuesta TEXT,
    monto_ofertado DECIMAL(10,2),
    tiempo_estimado VARCHAR(50),
    fecha_postulacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('pendiente','aceptada','rechazada') DEFAULT 'pendiente',
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    FOREIGN KEY (id_estudiante) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- CONTRATOS ENTRE CLIENTE Y ESTUDIANTE
-- ===========================
CREATE TABLE contratos (
    id_contrato INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    id_estudiante INT NOT NULL,
    id_cliente INT NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE,
    total_pago DECIMAL(10,2),
    estado ENUM('activo','completado','cancelado') DEFAULT 'activo',
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    FOREIGN KEY (id_estudiante) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario)
);

-- ===========================
-- MENSAJES / CHAT ENTRE USUARIOS
-- ===========================
CREATE TABLE mensajes (
    id_mensaje INT AUTO_INCREMENT PRIMARY KEY,
    id_emisor INT NOT NULL,
    id_receptor INT NOT NULL,
    id_proyecto INT,
    contenido TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_emisor) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_receptor) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto)
);

-- ===========================
-- VALORACIONES Y RESEÑAS
-- ===========================
CREATE TABLE valoraciones (
    id_valoracion INT AUTO_INCREMENT PRIMARY KEY,
    id_emisor INT NOT NULL,
    id_receptor INT NOT NULL,
    id_proyecto INT,
    calificacion INT CHECK (calificacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_emisor) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_receptor) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto)
);

-- ===========================
-- SISTEMA DE NOTIFICACIONES
-- ===========================
CREATE TABLE notificaciones (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo ENUM('mensaje','proyecto','valoracion','sistema') DEFAULT 'sistema',
    contenido TEXT,
    leido BOOLEAN DEFAULT FALSE,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ===========================
-- UBICACIONES (PARA GEOLOCALIZACIÓN)
-- ===========================
CREATE TABLE ubicaciones (
    id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    latitud DECIMAL(10,7),
    longitud DECIMAL(10,7),
    ciudad VARCHAR(100),
    departamento VARCHAR(100),
    pais VARCHAR(100) DEFAULT 'El Salvador',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ===========================
-- REPORTES / SOPORTE TÉCNICO
-- ===========================
CREATE TABLE reportes (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo ENUM('bug','fraude','soporte','otro') DEFAULT 'otro',
    descripcion TEXT,
    estado ENUM('pendiente','en_revision','resuelto') DEFAULT 'pendiente',
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
