-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 18, 2025 at 05:21 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

DROP DATABASE IF EXISTS hirelance_db;
CREATE DATABASE IF NOT EXISTS hirelance_db;
USE hirelance_db;


SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hirelance_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `categorias`
--



CREATE TABLE `categorias` (
                              `id_categoria` int(11) NOT NULL,
                              `nombre` varchar(255) DEFAULT NULL,
                              `descripcion` text DEFAULT NULL,
                              `icono` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categorias`
--

INSERT INTO `categorias` (`id_categoria`, `nombre`, `descripcion`, `icono`) VALUES
                                                                                (1, 'Desarrollo de Software', 'Web, Móvil, Backend, Frontend', NULL),
                                                                                (2, 'Diseño y Multimedia', 'Branding, UI/UX, Edición de Video', NULL),
                                                                                (3, 'Marketing Digital', 'SEO, SEM, Redes Sociales', NULL),
                                                                                (4, 'Redacción y Traducción', 'Copywriting, Traducción Técnica', NULL),
                                                                                (5, 'Asistencia Virtual', 'Data Entry, Soporte al Cliente', NULL),
                                                                                (6, 'Ingeniería y Arquitectura', 'Planos, CAD, Modelado 3D', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `contratos`
--

CREATE TABLE `contratos` (
                             `id_contrato` int(11) NOT NULL,
                             `id_proyecto` int(11) NOT NULL,
                             `id_estudiante` int(11) NOT NULL,
                             `id_contratista` int(11) NOT NULL,
                             `fecha_inicio` date DEFAULT NULL,
                             `fecha_fin` date DEFAULT NULL,
                             `total_pago` decimal(38,2) DEFAULT NULL,
                             `estado` enum('activo','completado','cancelado') DEFAULT 'activo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contratos`
--

INSERT INTO `contratos` (`id_contrato`, `id_proyecto`, `id_estudiante`, `id_contratista`, `fecha_inicio`, `fecha_fin`, `total_pago`, `estado`) VALUES
                                                                                                                                                   (1, 6, 2, 11, '2025-11-01', NULL, 1500.00, 'activo'),
                                                                                                                                                   (2, 7, 3, 12, '2025-11-10', NULL, 250.00, 'activo'),
                                                                                                                                                   (3, 8, 3, 13, '2025-09-28', '2025-10-01', 100.00, 'completado');

-- --------------------------------------------------------

--
-- Table structure for table `estudiante_universidad`
--

CREATE TABLE `estudiante_universidad` (
                                          `id_usuario` int(11) NOT NULL,
                                          `id_universidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `estudiante_universidad`
--

INSERT INTO `estudiante_universidad` (`id_usuario`, `id_universidad`) VALUES
                                                                          (2, 1),
                                                                          (3, 5),
                                                                          (4, 3),
                                                                          (5, 2),
                                                                          (6, 1);

-- --------------------------------------------------------

--
-- Table structure for table `habilidad`
--

CREATE TABLE `habilidad` (
                             `id_habilidad` int(11) NOT NULL,
                             `titulo` varchar(255) DEFAULT NULL,
                             `descripcion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `habilidad`
--

INSERT INTO `habilidad` (`id_habilidad`, `titulo`, `descripcion`) VALUES
                                                                      (1, 'Java Spring Boot', 'Backend'),
                                                                      (2, 'React Native', 'Mobile'),
                                                                      (3, 'Angular', 'Frontend'),
                                                                      (4, 'Adobe Photoshop', 'Diseño'),
                                                                      (5, 'Figma', 'Prototipado'),
                                                                      (6, 'Inglés C1', 'Idiomas'),
                                                                      (7, 'Google Ads', 'Marketing'),
                                                                      (8, 'Python', 'Data Science'),
                                                                      (9, 'Excel Avanzado', 'Ofimática'),
                                                                      (10, 'Atención al Cliente', 'Soft Skill');

-- --------------------------------------------------------

--
-- Table structure for table `habilidad_estudiante`
--

CREATE TABLE `habilidad_estudiante` (
                                        `id_usuario` int(11) NOT NULL,
                                        `id_habilidad` int(11) NOT NULL,
                                        `nivel` varchar(50) DEFAULT 'Intermedio' COMMENT 'Ej: Principiante, Intermedio, Avanzado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `habilidad_estudiante`
--

INSERT INTO `habilidad_estudiante` (`id_usuario`, `id_habilidad`, `nivel`) VALUES
                                                                               (2, 1, 'Avanzado'),
                                                                               (2, 10, 'Intermedio'),
                                                                               (3, 4, 'Experto'),
                                                                               (3, 5, 'Avanzado'),
                                                                               (4, 7, 'Avanzado'),
                                                                               (5, 6, 'Nativo'),
                                                                               (6, 2, 'Avanzado'),
                                                                               (6, 3, 'Intermedio');

-- --------------------------------------------------------

--
-- Table structure for table `mensajes`
--

CREATE TABLE `mensajes` (
                            `id_mensaje` int(11) NOT NULL,
                            `id_emisor` int(11) NOT NULL,
                            `id_receptor` int(11) NOT NULL,
                            `id_proyecto` int(11) DEFAULT NULL,
                            `contenido` text NOT NULL,
                            `fecha_envio` timestamp NOT NULL DEFAULT current_timestamp(),
                            `leido` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mensajes`
--

INSERT INTO `mensajes` (`id_mensaje`, `id_emisor`, `id_receptor`, `id_proyecto`, `contenido`, `fecha_envio`, `leido`) VALUES
                                                                                                                          (1, 11, 2, NULL, 'Hola Juan, bienvenido al proyecto.', '2025-11-18 04:12:45', 1),
                                                                                                                          (2, 2, 11, NULL, 'Gracias Carlos, ya estoy trabajando.', '2025-11-18 04:12:45', 1),
                                                                                                                          (3, 11, 2, NULL, 'Perfecto, avísame cualquier duda.', '2025-11-18 04:12:45', 0);

-- --------------------------------------------------------

--
-- Table structure for table `notificaciones`
--

CREATE TABLE `notificaciones` (
                                  `id_notificacion` int(11) NOT NULL,
                                  `id_usuario` int(11) NOT NULL,
                                  `tipo` enum('mensaje','proyecto','valoracion','sistema') DEFAULT 'sistema',
                                  `contenido` text DEFAULT NULL,
                                  `enlace` varchar(255) DEFAULT NULL,
                                  `leido` tinyint(1) DEFAULT 0,
                                  `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notificaciones`
--

INSERT INTO `notificaciones` (`id_notificacion`, `id_usuario`, `tipo`, `contenido`, `enlace`, `leido`, `fecha`) VALUES
                                                                                                                    (1, 2, 'proyecto', 'Nuevo proyecto compatible: App de Repartidores', '/student/project/5/apply', 0, '2025-11-18 04:12:45'),
                                                                                                                    (2, 2, 'proyecto', '¡Felicidades! Contrato generado para API Rest.', '/student/contracts', 0, '2025-11-18 04:12:45'),
                                                                                                                    (3, 11, 'proyecto', 'Nueva postulación de Luis en Sistema Inventarios.', '/client/application/5/details', 0, '2025-11-18 04:12:45'),
                                                                                                                    (4, 2, 'mensaje', 'Tienes un nuevo mensaje de Carlos.', '/chat/11', 0, '2025-11-18 04:12:45');

-- --------------------------------------------------------

--
-- Table structure for table `perfil_contratista`
--

CREATE TABLE `perfil_contratista` (
                                      `id_perfil` int(11) NOT NULL,
                                      `id_usuario` int(11) NOT NULL,
                                      `empresa` varchar(255) DEFAULT NULL,
                                      `ubicacion` varchar(255) DEFAULT NULL,
                                      `descripcion` text DEFAULT NULL,
                                      `sitio_web` varchar(255) DEFAULT NULL,
                                      `logo_empresa` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `perfil_contratista`
--

INSERT INTO `perfil_contratista` (`id_perfil`, `id_usuario`, `empresa`, `ubicacion`, `descripcion`, `sitio_web`, `logo_empresa`) VALUES
                                                                                                                                     (1, 11, 'Tech Solutions', 'San Salvador', 'Desarrollo de software a medida.', NULL, NULL),
                                                                                                                                     (2, 12, 'Sabor Local', 'Santa Tecla', 'Restaurante de comida fusión.', NULL, NULL),
                                                                                                                                     (3, 13, 'Moda Express', 'San Miguel', 'Tienda de ropa en línea.', NULL, NULL),
                                                                                                                                     (4, 14, 'Banco Futuro', 'Antiguo Cuscatlán', 'Institución financiera moderna.', NULL, NULL),
                                                                                                                                     (5, 15, 'Innova App', 'San Salvador', 'App de delivery local.', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `perfil_estudiante`
--

CREATE TABLE `perfil_estudiante` (
                                     `id_perfil` int(11) NOT NULL,
                                     `id_usuario` int(11) NOT NULL,
                                     `carrera` varchar(255) DEFAULT NULL,
                                     `anio_carrera` int(11) DEFAULT NULL,
                                     `descripcion` text DEFAULT NULL,
                                     `portafolio_url` varchar(255) DEFAULT NULL,
                                     `foto_perfil` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `perfil_estudiante`
--

INSERT INTO `perfil_estudiante` (`id_perfil`, `id_usuario`, `carrera`, `anio_carrera`, `descripcion`, `portafolio_url`, `foto_perfil`) VALUES
                                                                                                                                           (1, 2, 'Ingeniería en Sistemas', 5, 'Experto en Backend Java y Microservicios.', NULL, NULL),
                                                                                                                                           (2, 3, 'Diseño Gráfico', 3, 'Apasionada por el branding minimalista.', NULL, NULL),
                                                                                                                                           (3, 4, 'Licenciatura en Mercadeo', 4, 'Especialista en crecimiento orgánico y pauta.', NULL, NULL),
                                                                                                                                           (4, 5, 'Licenciatura en Idiomas', 2, 'Traductora técnica certificada.', NULL, NULL),
                                                                                                                                           (5, 6, 'Ingeniería en Ciencias de la Computación', 4, 'Frontend Developer React/Vue.', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `postulaciones`
--

CREATE TABLE `postulaciones` (
                                 `id_postulacion` int(11) NOT NULL,
                                 `id_proyecto` int(11) NOT NULL,
                                 `id_estudiante` int(11) NOT NULL,
                                 `propuesta` text DEFAULT NULL,
                                 `monto_ofertado` double DEFAULT NULL,
                                 `tiempo_estimado` varchar(255) DEFAULT NULL,
                                 `fecha_postulacion` timestamp NOT NULL DEFAULT current_timestamp(),
                                 `estado` enum('pendiente','aceptada','rechazada') DEFAULT 'pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `postulaciones`
--

INSERT INTO `postulaciones` (`id_postulacion`, `id_proyecto`, `id_estudiante`, `propuesta`, `monto_ofertado`, `tiempo_estimado`, `fecha_postulacion`, `estado`) VALUES
                                                                                                                                                                    (1, 6, 2, 'Soy experto en Spring Boot y Facturación.', 1500, '3 semanas', '2025-11-18 04:12:45', 'aceptada'),
                                                                                                                                                                    (2, 7, 3, 'Llevo equipo profesional.', 250, '1 día', '2025-11-18 04:12:45', 'aceptada'),
                                                                                                                                                                    (3, 8, 3, 'Entrego vectores y PNG.', 100, '3 días', '2025-11-18 04:12:45', 'aceptada'),
                                                                                                                                                                    (4, 1, 2, 'Me interesa el inventario.', 800, '1 mes', '2025-11-18 04:12:45', 'pendiente'),
                                                                                                                                                                    (5, 1, 6, 'Puedo hacerlo en React y Node.', 750, '3 semanas', '2025-11-18 04:12:45', 'pendiente'),
                                                                                                                                                                    (6, 3, 4, 'Experto en Facebook Ads.', 300, '1 mes', '2025-11-18 04:12:45', 'pendiente');

-- --------------------------------------------------------

--
-- Table structure for table `proyectos`
--

CREATE TABLE `proyectos` (
                             `id_proyecto` int(11) NOT NULL,
                             `id_contratista` int(11) NOT NULL,
                             `id_categoria` int(11) DEFAULT NULL,
                             `titulo` varchar(255) DEFAULT NULL,
                             `descripcion` text NOT NULL,
                             `presupuesto` double DEFAULT NULL,
                             `fecha_publicacion` timestamp NOT NULL DEFAULT current_timestamp(),
                             `fecha_limite` datetime(6) DEFAULT NULL,
                             `estado` enum('publicado','en_progreso','finalizado','cancelado') DEFAULT 'publicado',
                             `fecha_creacion` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `proyectos`
--

INSERT INTO `proyectos` (`id_proyecto`, `id_contratista`, `id_categoria`, `titulo`, `descripcion`, `presupuesto`, `fecha_publicacion`, `fecha_limite`, `estado`, `fecha_creacion`) VALUES
                                                                                                                                                                                       (1, 11, 1, 'Sistema de Inventarios', 'Sistema web para control de stock con Java.', 800, '2025-11-18 04:12:45', '2025-12-31 00:00:00.000000', 'publicado', NULL),
                                                                                                                                                                                       (2, 12, 2, 'Diseño de Menú Digital', 'PDF interactivo y QR para mesas.', 150, '2025-11-18 04:12:45', '2025-11-20 00:00:00.000000', 'publicado', NULL),
                                                                                                                                                                                       (3, 13, 3, 'Campaña Facebook Ads', 'Gestión de publicidad por un mes.', 300, '2025-11-18 04:12:45', '2025-12-05 00:00:00.000000', 'publicado', NULL),
                                                                                                                                                                                       (4, 14, 5, 'Digitalización de Archivos', 'Pasar expedientes físicos a Excel.', 500, '2025-11-18 04:12:45', '2025-11-30 00:00:00.000000', 'publicado', NULL),
                                                                                                                                                                                       (5, 15, 1, 'App de Repartidores', 'Módulo de GPS para Android.', 1200, '2025-11-18 04:12:45', '2026-01-15 00:00:00.000000', 'publicado', NULL),
                                                                                                                                                                                       (6, 11, 1, 'API Rest Facturación', 'Integración con Hacienda.', 1500, '2025-11-18 04:12:45', '2025-11-25 00:00:00.000000', 'en_progreso', NULL),
                                                                                                                                                                                       (7, 12, 2, 'Fotografía de Platillos', 'Sesión de fotos para redes.', 250, '2025-11-18 04:12:45', '2025-11-18 00:00:00.000000', 'en_progreso', NULL),
                                                                                                                                                                                       (8, 13, 2, 'Logo Tienda Ropa', 'Rediseño de imagen corporativa.', 100, '2025-11-18 04:12:45', '2025-10-01 00:00:00.000000', 'finalizado', NULL),
                                                                                                                                                                                       (9, 11, 4, 'Traducción Manual Técnico', 'Inglés a Español, 50 págs.', 200, '2025-11-18 04:12:45', '2025-09-15 00:00:00.000000', 'finalizado', NULL),
                                                                                                                                                                                       (10, 15, 3, 'Community Manager', 'Cancelado por presupuesto.', 400, '2025-11-18 04:12:45', '2025-10-10 00:00:00.000000', 'cancelado', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `proyecto_habilidad`
--

CREATE TABLE `proyecto_habilidad` (
                                      `id_proyecto` int(11) NOT NULL,
                                      `id_habilidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reportes`
--

CREATE TABLE `reportes` (
                            `id_reporte` int(11) NOT NULL,
                            `id_usuario` int(11) NOT NULL,
                            `tipo` enum('bug','fraude','soporte','otro') DEFAULT 'otro',
                            `descripcion` text DEFAULT NULL,
                            `estado` enum('pendiente','en_revision','resuelto') DEFAULT 'pendiente',
                            `fecha_reporte` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reportes`
--

INSERT INTO `reportes` (`id_reporte`, `id_usuario`, `tipo`, `descripcion`, `estado`, `fecha_reporte`) VALUES
                                                                                                          (1, 2, 'bug', 'Error al subir foto de perfil.', 'pendiente', '2025-11-18 04:12:45'),
                                                                                                          (2, 11, 'soporte', 'Solicito factura fiscal.', 'en_revision', '2025-11-18 04:12:45');

-- --------------------------------------------------------

--
-- Table structure for table `ubicaciones`
--

CREATE TABLE `ubicaciones` (
                               `id_ubicacion` int(11) NOT NULL,
                               `id_usuario` int(11) NOT NULL,
                               `ciudad` varchar(255) DEFAULT NULL,
                               `departamento` varchar(255) DEFAULT NULL,
                               `pais` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ubicaciones`
--

INSERT INTO `ubicaciones` (`id_ubicacion`, `id_usuario`, `ciudad`, `departamento`, `pais`) VALUES
                                                                                               (1, 2, 'San Salvador', 'San Salvador', NULL),
                                                                                               (2, 3, 'Santa Tecla', 'La Libertad', NULL),
                                                                                               (3, 4, 'Soyapango', 'San Salvador', NULL),
                                                                                               (4, 5, 'Santa Ana', 'Santa Ana', NULL),
                                                                                               (5, 6, 'San Miguel', 'San Miguel', NULL),
                                                                                               (6, 7, 'Ahuachapán', 'Ahuachapán', NULL),
                                                                                               (7, 8, 'San Salvador', 'San Salvador', NULL),
                                                                                               (8, 9, 'Santa Tecla', 'La Libertad', NULL),
                                                                                               (9, 10, 'Sonsonate', 'Sonsonate', NULL),
                                                                                               (10, 11, 'San Salvador', 'San Salvador', NULL),
                                                                                               (11, 12, 'Santa Tecla', 'La Libertad', NULL),
                                                                                               (12, 13, 'San Miguel', 'San Miguel', NULL),
                                                                                               (13, 14, 'Antiguo Cuscatlán', 'La Libertad', NULL),
                                                                                               (14, 15, 'San Salvador', 'San Salvador', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `universidad`
--

CREATE TABLE `universidad` (
                               `id_universidad` int(11) NOT NULL,
                               `nombre` varchar(255) NOT NULL,
                               `logo` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `universidad`
--

INSERT INTO `universidad` (`id_universidad`, `nombre`, `logo`) VALUES
                                                                   (1, 'Universidad de El Salvador (UES)', NULL),
                                                                   (2, 'Universidad Centroamericana (UCA)', NULL),
                                                                   (3, 'Universidad Don Bosco (UDB)', NULL),
                                                                   (4, 'Universidad Tecnológica (UTEC)', NULL),
                                                                   (5, 'Escuela de Comunicación Mónica Herrera', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

CREATE TABLE `usuarios` (
                            `id_usuario` int(11) NOT NULL,
                            `nombre` varchar(255) DEFAULT NULL,
                            `apellido` varchar(255) DEFAULT NULL,
                            `correo` varchar(255) DEFAULT NULL,
                            `contrasena` varchar(255) NOT NULL,
                            `dui` varchar(10) NOT NULL COMMENT 'Documento Único de Identidad (formato NNNNNNNN-N)',
                            `tipo` enum('estudiante','contratista','admin') DEFAULT NULL,
                            `telefono` varchar(14) DEFAULT NULL,
                            `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
                            `estado` enum('activo','inactivo','baneado') DEFAULT 'activo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `correo`, `contrasena`, `dui`, `tipo`, `telefono`, `fecha_registro`, `estado`) VALUES
                                                                                                                                               (1, 'Super', 'Admin', 'admin@hirelance.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000000-0', 'admin', '2222-0000', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (2, 'Juan', 'Perez', 'juan@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000001-1', 'estudiante', '6001-0001', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (3, 'Maria', 'Gomez', 'maria@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000002-2', 'estudiante', '6002-0002', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (4, 'Pedro', 'Rivas', 'pedro@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000003-3', 'estudiante', '6003-0003', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (5, 'Ana', 'Torres', 'ana@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000004-4', 'estudiante', '6004-0004', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (6, 'Luis', 'Diaz', 'luis@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000005-5', 'estudiante', '6005-0005', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (7, 'Carla', 'Mendez', 'carla@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000006-6', 'estudiante', '6006-0006', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (8, 'Diego', 'Luna', 'diego@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000007-7', 'estudiante', '6007-0007', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (9, 'Sofia', 'Castillo', 'sofia@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000008-8', 'estudiante', '6008-0008', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (10, 'Javier', 'Sosa', 'javier@estudiante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '00000009-9', 'estudiante', '6009-0009', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (11, 'Carlos', 'CEO', 'carlos@techsolutions.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '10000001-1', 'contratista', '7001-0001', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (12, 'Elena', 'Dueña', 'elena@restaurante.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '10000002-2', 'contratista', '7002-0002', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (13, 'Roberto', 'Gerente', 'roberto@tienda.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '10000003-3', 'contratista', '7003-0003', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (14, 'Laura', 'HR', 'laura@banco.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '10000004-4', 'contratista', '7004-0004', '2025-11-18 04:12:45', 'activo'),
                                                                                                                                               (15, 'Mario', 'Founder', 'mario@startup.com', '$2a$10$AzpUNEOBjLsDDSsnJjKlfuZIo3lQa4epetW1M8PNnPcSW9cS7cena', '10000005-5', 'contratista', '7005-0005', '2025-11-18 04:12:45', 'activo');

-- --------------------------------------------------------

--
-- Table structure for table `valoraciones`
--

CREATE TABLE `valoraciones` (
                                `id_valoracion` int(11) NOT NULL,
                                `id_emisor` int(11) NOT NULL,
                                `id_receptor` int(11) NOT NULL,
                                `id_proyecto` int(11) DEFAULT NULL,
                                `calificacion` int(11) DEFAULT NULL CHECK (`calificacion` between 1 and 5),
                                `comentario` text DEFAULT NULL,
                                `fecha` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `valoraciones`
--

INSERT INTO `valoraciones` (`id_valoracion`, `id_emisor`, `id_receptor`, `id_proyecto`, `calificacion`, `comentario`, `fecha`) VALUES
    (1, 13, 3, 8, 5, 'Excelente trabajo de Maria, muy creativa.', '2025-11-18 04:12:45');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categorias`
--
ALTER TABLE `categorias`
    ADD PRIMARY KEY (`id_categoria`);

--
-- Indexes for table `contratos`
--
ALTER TABLE `contratos`
    ADD PRIMARY KEY (`id_contrato`),
  ADD KEY `id_proyecto` (`id_proyecto`),
  ADD KEY `id_estudiante` (`id_estudiante`),
  ADD KEY `id_contratista` (`id_contratista`);

--
-- Indexes for table `estudiante_universidad`
--
ALTER TABLE `estudiante_universidad`
    ADD PRIMARY KEY (`id_usuario`,`id_universidad`),
  ADD KEY `id_universidad` (`id_universidad`);

--
-- Indexes for table `habilidad`
--
ALTER TABLE `habilidad`
    ADD PRIMARY KEY (`id_habilidad`),
  ADD UNIQUE KEY `titulo` (`titulo`);

--
-- Indexes for table `habilidad_estudiante`
--
ALTER TABLE `habilidad_estudiante`
    ADD PRIMARY KEY (`id_usuario`,`id_habilidad`),
  ADD KEY `id_habilidad` (`id_habilidad`);

--
-- Indexes for table `mensajes`
--
ALTER TABLE `mensajes`
    ADD PRIMARY KEY (`id_mensaje`),
  ADD KEY `id_emisor` (`id_emisor`),
  ADD KEY `id_receptor` (`id_receptor`),
  ADD KEY `id_proyecto` (`id_proyecto`);

--
-- Indexes for table `notificaciones`
--
ALTER TABLE `notificaciones`
    ADD PRIMARY KEY (`id_notificacion`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `perfil_contratista`
--
ALTER TABLE `perfil_contratista`
    ADD PRIMARY KEY (`id_perfil`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `perfil_estudiante`
--
ALTER TABLE `perfil_estudiante`
    ADD PRIMARY KEY (`id_perfil`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `postulaciones`
--
ALTER TABLE `postulaciones`
    ADD PRIMARY KEY (`id_postulacion`),
  ADD KEY `id_proyecto` (`id_proyecto`),
  ADD KEY `id_estudiante` (`id_estudiante`);

--
-- Indexes for table `proyectos`
--
ALTER TABLE `proyectos`
    ADD PRIMARY KEY (`id_proyecto`),
  ADD KEY `id_contratista` (`id_contratista`),
  ADD KEY `id_categoria` (`id_categoria`);

--
-- Indexes for table `proyecto_habilidad`
--
ALTER TABLE `proyecto_habilidad`
    ADD PRIMARY KEY (`id_proyecto`,`id_habilidad`),
  ADD KEY `FKffvnrg5hpccb10n32mryr17rw` (`id_habilidad`);

--
-- Indexes for table `reportes`
--
ALTER TABLE `reportes`
    ADD PRIMARY KEY (`id_reporte`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `ubicaciones`
--
ALTER TABLE `ubicaciones`
    ADD PRIMARY KEY (`id_ubicacion`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `universidad`
--
ALTER TABLE `universidad`
    ADD PRIMARY KEY (`id_universidad`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
    ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- Indexes for table `valoraciones`
--
ALTER TABLE `valoraciones`
    ADD PRIMARY KEY (`id_valoracion`),
  ADD KEY `id_emisor` (`id_emisor`),
  ADD KEY `id_receptor` (`id_receptor`),
  ADD KEY `id_proyecto` (`id_proyecto`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categorias`
--
ALTER TABLE `categorias`
    MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `contratos`
--
ALTER TABLE `contratos`
    MODIFY `id_contrato` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `habilidad`
--
ALTER TABLE `habilidad`
    MODIFY `id_habilidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `mensajes`
--
ALTER TABLE `mensajes`
    MODIFY `id_mensaje` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `notificaciones`
--
ALTER TABLE `notificaciones`
    MODIFY `id_notificacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `perfil_contratista`
--
ALTER TABLE `perfil_contratista`
    MODIFY `id_perfil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `perfil_estudiante`
--
ALTER TABLE `perfil_estudiante`
    MODIFY `id_perfil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `postulaciones`
--
ALTER TABLE `postulaciones`
    MODIFY `id_postulacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `proyectos`
--
ALTER TABLE `proyectos`
    MODIFY `id_proyecto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `reportes`
--
ALTER TABLE `reportes`
    MODIFY `id_reporte` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `ubicaciones`
--
ALTER TABLE `ubicaciones`
    MODIFY `id_ubicacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `universidad`
--
ALTER TABLE `universidad`
    MODIFY `id_universidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
    MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `valoraciones`
--
ALTER TABLE `valoraciones`
    MODIFY `id_valoracion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `contratos`
--
ALTER TABLE `contratos`
    ADD CONSTRAINT `contratos_ibfk_1` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`),
  ADD CONSTRAINT `contratos_ibfk_2` FOREIGN KEY (`id_estudiante`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `contratos_ibfk_3` FOREIGN KEY (`id_contratista`) REFERENCES `usuarios` (`id_usuario`);

--
-- Constraints for table `estudiante_universidad`
--
ALTER TABLE `estudiante_universidad`
    ADD CONSTRAINT `estudiante_universidad_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `estudiante_universidad_ibfk_2` FOREIGN KEY (`id_universidad`) REFERENCES `universidad` (`id_universidad`) ON DELETE CASCADE;

--
-- Constraints for table `habilidad_estudiante`
--
ALTER TABLE `habilidad_estudiante`
    ADD CONSTRAINT `habilidad_estudiante_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `habilidad_estudiante_ibfk_2` FOREIGN KEY (`id_habilidad`) REFERENCES `habilidad` (`id_habilidad`) ON DELETE CASCADE;

--
-- Constraints for table `mensajes`
--
ALTER TABLE `mensajes`
    ADD CONSTRAINT `mensajes_ibfk_1` FOREIGN KEY (`id_emisor`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `mensajes_ibfk_2` FOREIGN KEY (`id_receptor`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `mensajes_ibfk_3` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`);

--
-- Constraints for table `notificaciones`
--
ALTER TABLE `notificaciones`
    ADD CONSTRAINT `notificaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

--
-- Constraints for table `perfil_contratista`
--
ALTER TABLE `perfil_contratista`
    ADD CONSTRAINT `perfil_contratista_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Constraints for table `perfil_estudiante`
--
ALTER TABLE `perfil_estudiante`
    ADD CONSTRAINT `perfil_estudiante_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Constraints for table `postulaciones`
--
ALTER TABLE `postulaciones`
    ADD CONSTRAINT `postulaciones_ibfk_1` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`) ON DELETE CASCADE,
  ADD CONSTRAINT `postulaciones_ibfk_2` FOREIGN KEY (`id_estudiante`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Constraints for table `proyectos`
--
ALTER TABLE `proyectos`
    ADD CONSTRAINT `proyectos_ibfk_1` FOREIGN KEY (`id_contratista`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `proyectos_ibfk_2` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`);

--
-- Constraints for table `proyecto_habilidad`
--
ALTER TABLE `proyecto_habilidad`
    ADD CONSTRAINT `FKffvnrg5hpccb10n32mryr17rw` FOREIGN KEY (`id_habilidad`) REFERENCES `habilidad` (`id_habilidad`),
  ADD CONSTRAINT `FKh9ngr2jr4ui7yhwb6fie1ocib` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`);

--
-- Constraints for table `reportes`
--
ALTER TABLE `reportes`
    ADD CONSTRAINT `reportes_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

--
-- Constraints for table `ubicaciones`
--
ALTER TABLE `ubicaciones`
    ADD CONSTRAINT `ubicaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Constraints for table `valoraciones`
--
ALTER TABLE `valoraciones`
    ADD CONSTRAINT `valoraciones_ibfk_1` FOREIGN KEY (`id_emisor`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `valoraciones_ibfk_2` FOREIGN KEY (`id_receptor`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `valoraciones_ibfk_3` FOREIGN KEY (`id_proyecto`) REFERENCES `proyectos` (`id_proyecto`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
