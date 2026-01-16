-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-01-2026 a las 20:19:26
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `farmacia_salud`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `domicilios`
--

CREATE TABLE `domicilios` (
  `id` bigint(20) NOT NULL,
  `persona_id` bigint(20) NOT NULL,
  `direccion` varchar(255) NOT NULL,
  `barrio` varchar(100) DEFAULT NULL,
  `localidad` varchar(100) DEFAULT NULL,
  `provincia` varchar(100) DEFAULT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `tipo` enum('CASA','TRABAJO','OTRO') DEFAULT 'CASA'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `entregas`
--

CREATE TABLE `entregas` (
  `id` bigint(20) NOT NULL,
  `receta_id` bigint(20) DEFAULT NULL,
  `persona_id` bigint(20) NOT NULL,
  `usuario_entrega_id` bigint(20) NOT NULL,
  `fecha_entrega` timestamp NOT NULL DEFAULT current_timestamp(),
  `observaciones` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `entrega_detalles`
--

CREATE TABLE `entrega_detalles` (
  `id` bigint(20) NOT NULL,
  `entrega_id` bigint(20) NOT NULL,
  `lote_id` bigint(20) NOT NULL,
  `cantidad_entregada` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historias_clinicas`
--

CREATE TABLE `historias_clinicas` (
  `id` bigint(20) NOT NULL,
  `persona_id` bigint(20) NOT NULL,
  `medico_id` bigint(20) NOT NULL,
  `fecha_consulta` timestamp NOT NULL DEFAULT current_timestamp(),
  `motivo_consulta` text DEFAULT NULL,
  `diagnostico` text DEFAULT NULL,
  `observaciones` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lotes`
--

CREATE TABLE `lotes` (
  `id` bigint(20) NOT NULL,
  `medicamento_id` bigint(20) NOT NULL,
  `numero_lote` varchar(50) NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `cantidad_inicial` int(11) NOT NULL,
  `cantidad_actual` int(11) NOT NULL,
  `fecha_ingreso` timestamp NOT NULL DEFAULT current_timestamp(),
  `precio_compra` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `lotes`
--

INSERT INTO `lotes` (`id`, `medicamento_id`, `numero_lote`, `fecha_vencimiento`, `cantidad_inicial`, `cantidad_actual`, `fecha_ingreso`, `precio_compra`) VALUES
(1, 1, 'LOT001', '2026-12-31', 1000, 1000, '2026-01-15 19:06:08', 50.00),
(2, 2, 'LOT002', '2026-06-30', 500, 500, '2026-01-15 19:06:08', 75.00),
(3, 3, 'LOT003', '2025-12-31', 200, 200, '2026-01-15 19:06:08', 150.00),
(4, 4, 'LOT004', '2027-03-31', 800, 800, '2026-01-15 19:06:08', 45.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

CREATE TABLE `medicamentos` (
  `id` bigint(20) NOT NULL,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `nombre_comercial` varchar(150) NOT NULL,
  `nombre_generico` varchar(150) DEFAULT NULL,
  `laboratorio` varchar(100) DEFAULT NULL,
  `forma_farmaceutica` enum('COMPRIMIDO','CAPSULA','JARABE','CREMA','INYECTABLE','GOTAS','PARCHE','OTRO') DEFAULT NULL,
  `concentracion` varchar(50) DEFAULT NULL,
  `unidad_presentacion` enum('UNIDAD','BLISTER','FRASCO','TUBO','SOBRES') DEFAULT NULL,
  `requiere_receta` tinyint(1) DEFAULT 0,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`id`, `codigo_barras`, `nombre_comercial`, `nombre_generico`, `laboratorio`, `forma_farmaceutica`, `concentracion`, `unidad_presentacion`, `requiere_receta`, `activo`) VALUES
(1, '7501031311709', 'Paracetamol 500mg', 'Paracetamol', 'Bayer', 'COMPRIMIDO', '500mg', 'BLISTER', 0, 1),
(2, '7501580120117', 'Ibuprofeno 400mg', 'Ibuprofeno', 'Genomma', 'COMPRIMIDO', '400mg', 'BLISTER', 0, 1),
(3, '7502211964226', 'Amoxicilina 500mg', 'Amoxicilina', 'Pfizer', 'CAPSULA', '500mg', 'BLISTER', 1, 1),
(4, '7501031311716', 'Aspirina 100mg', 'Ácido Acetilsalicílico', 'Bayer', 'COMPRIMIDO', '100mg', 'BLISTER', 0, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimientos_stock`
--

CREATE TABLE `movimientos_stock` (
  `id` bigint(20) NOT NULL,
  `lote_id` bigint(20) NOT NULL,
  `tipo_movimiento` enum('INGRESO','EGRESO','AJUSTE_POSITIVO','AJUSTE_NEGATIVO','VENCIMIENTO') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  `fecha_movimiento` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personas`
--

CREATE TABLE `personas` (
  `id` bigint(20) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `tipo_documento` enum('DNI','LC','LE','PASAPORTE') DEFAULT 'DNI',
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `sexo` enum('M','F','OTRO') DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `personas`
--

INSERT INTO `personas` (`id`, `numero_documento`, `tipo_documento`, `nombre`, `apellido`, `fecha_nacimiento`, `sexo`, `telefono`, `email`, `activo`) VALUES
(1, '30123456', 'DNI', 'Ana', 'Martínez', '1985-03-15', 'F', '351-1234567', NULL, 1),
(2, '30234567', 'DNI', 'Carlos', 'Rodríguez', '1990-07-22', 'M', '351-2345678', NULL, 1),
(3, '30345678', 'DNI', 'Elena', 'Fernández', '1978-11-08', 'F', '351-3456789', NULL, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recetas`
--

CREATE TABLE `recetas` (
  `id` bigint(20) NOT NULL,
  `historia_clinica_id` bigint(20) NOT NULL,
  `medico_id` bigint(20) NOT NULL,
  `persona_id` bigint(20) NOT NULL,
  `fecha_emision` timestamp NOT NULL DEFAULT current_timestamp(),
  `vigencia_dias` int(11) DEFAULT 30,
  `observaciones` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `rol` enum('MEDICO','FARMACEUTICO','ADMINISTRADOR','AGENTE') NOT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `email`, `password_hash`, `nombre`, `apellido`, `rol`, `activo`, `fecha_creacion`) VALUES
(1, 'admin@farmacia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.c7ECNq/3bJz5LQw.6q', 'Administrador', 'Sistema', 'ADMINISTRADOR', 1, '2026-01-15 19:06:08'),
(2, 'medico@farmacia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.c7ECNq/3bJz5LQw.6q', 'Juan', 'Pérez', 'MEDICO', 1, '2026-01-15 19:06:08'),
(3, 'farmaceutico@farmacia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.c7ECNq/3bJz5LQw.6q', 'María', 'García', 'FARMACEUTICO', 1, '2026-01-15 19:06:08'),
(4, 'agente@farmacia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.c7ECNq/3bJz5LQw.6q', 'Pedro', 'López', 'AGENTE', 1, '2026-01-15 19:06:08');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `domicilios`
--
ALTER TABLE `domicilios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_persona` (`persona_id`);

--
-- Indices de la tabla `entregas`
--
ALTER TABLE `entregas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_receta` (`receta_id`),
  ADD KEY `idx_persona` (`persona_id`),
  ADD KEY `idx_fecha` (`fecha_entrega`),
  ADD KEY `usuario_entrega_id` (`usuario_entrega_id`);

--
-- Indices de la tabla `entrega_detalles`
--
ALTER TABLE `entrega_detalles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_entrega` (`entrega_id`),
  ADD KEY `idx_lote` (`lote_id`);

--
-- Indices de la tabla `historias_clinicas`
--
ALTER TABLE `historias_clinicas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_persona` (`persona_id`),
  ADD KEY `idx_medico` (`medico_id`),
  ADD KEY `idx_fecha` (`fecha_consulta`);

--
-- Indices de la tabla `lotes`
--
ALTER TABLE `lotes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_medicamento` (`medicamento_id`),
  ADD KEY `idx_vencimiento` (`fecha_vencimiento`),
  ADD KEY `idx_lote` (`numero_lote`);

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_barras` (`codigo_barras`),
  ADD KEY `idx_nombre` (`nombre_comercial`),
  ADD KEY `idx_codigo_barras` (`codigo_barras`);

--
-- Indices de la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_lote` (`lote_id`),
  ADD KEY `idx_fecha` (`fecha_movimiento`),
  ADD KEY `idx_tipo` (`tipo_movimiento`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `personas`
--
ALTER TABLE `personas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_documento` (`numero_documento`),
  ADD KEY `idx_documento` (`numero_documento`),
  ADD KEY `idx_apellido` (`apellido`);

--
-- Indices de la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_historia` (`historia_clinica_id`),
  ADD KEY `idx_persona` (`persona_id`),
  ADD KEY `medico_id` (`medico_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_rol` (`rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `domicilios`
--
ALTER TABLE `domicilios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `entregas`
--
ALTER TABLE `entregas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `entrega_detalles`
--
ALTER TABLE `entrega_detalles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `historias_clinicas`
--
ALTER TABLE `historias_clinicas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `lotes`
--
ALTER TABLE `lotes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `personas`
--
ALTER TABLE `personas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `recetas`
--
ALTER TABLE `recetas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `domicilios`
--
ALTER TABLE `domicilios`
  ADD CONSTRAINT `domicilios_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `personas` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `entregas`
--
ALTER TABLE `entregas`
  ADD CONSTRAINT `entregas_ibfk_1` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`),
  ADD CONSTRAINT `entregas_ibfk_2` FOREIGN KEY (`persona_id`) REFERENCES `personas` (`id`),
  ADD CONSTRAINT `entregas_ibfk_3` FOREIGN KEY (`usuario_entrega_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `entrega_detalles`
--
ALTER TABLE `entrega_detalles`
  ADD CONSTRAINT `entrega_detalles_ibfk_1` FOREIGN KEY (`entrega_id`) REFERENCES `entregas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `entrega_detalles_ibfk_2` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`);

--
-- Filtros para la tabla `historias_clinicas`
--
ALTER TABLE `historias_clinicas`
  ADD CONSTRAINT `historias_clinicas_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `personas` (`id`),
  ADD CONSTRAINT `historias_clinicas_ibfk_2` FOREIGN KEY (`medico_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `lotes`
--
ALTER TABLE `lotes`
  ADD CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamentos` (`id`);

--
-- Filtros para la tabla `movimientos_stock`
--
ALTER TABLE `movimientos_stock`
  ADD CONSTRAINT `movimientos_stock_ibfk_1` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`),
  ADD CONSTRAINT `movimientos_stock_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `recetas`
--
ALTER TABLE `recetas`
  ADD CONSTRAINT `recetas_ibfk_1` FOREIGN KEY (`historia_clinica_id`) REFERENCES `historias_clinicas` (`id`),
  ADD CONSTRAINT `recetas_ibfk_2` FOREIGN KEY (`medico_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `recetas_ibfk_3` FOREIGN KEY (`persona_id`) REFERENCES `personas` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
