-- ====================================================================
-- BASE DE DATOS: db_restaurante
-- Curso: Diseño de Patrones (UTP) - Avance 50%
-- Líder Técnico: Lincoln Vega Hidalgo
-- ====================================================================

CREATE DATABASE IF NOT EXISTS db_restaurante CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_restaurante;

-- 1. Tabla de Canales de Venta (Salón, Delivery, Para Llevar)
DROP TABLE IF EXISTS canales;
CREATE TABLE canales (
    id_canal VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    recargo_base DECIMAL(10,2) DEFAULT 0.00,
    activo BOOLEAN DEFAULT TRUE
);

INSERT INTO canales (id_canal, nombre, recargo_base) VALUES
('SALON', 'Atención en Mesa / Salón', 0.00),
('DELIVERY', 'Envío a Domicilio', 5.00),
('PARA_LLEVAR', 'Recojo en Restaurante', 0.00);

-- 2. Tabla de Insumos de Almacén (Para el patrón Proxy de inventario)
DROP TABLE IF EXISTS insumos_almacen;
CREATE TABLE insumos_almacen (
    codigo_insumo VARCHAR(30) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    stock_disponible INT NOT NULL,
    unidad_medida VARCHAR(20) DEFAULT 'UNIDAD',
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO insumos_almacen (codigo_insumo, nombre, stock_disponible, unidad_medida) VALUES
('CARNE_RES', 'Carne de Res para Hamburguesa 150g', 100, 'UNIDAD'),
('PAPAS_FRITAS', 'Porción de Papas Fritas Amarillas', 250, 'PORCION'),
('QUESO_CHEDDAR', 'Láminas de Queso Cheddar Fundido', 300, 'LAMINA'),
('GASEOSA_500ML', 'Gaseosa Personal 500ml', 120, 'BOTELLA');

-- 3. Tabla de Platos y Productos de Carta
DROP TABLE IF EXISTS platos;
CREATE TABLE platos (
    id_plato VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

INSERT INTO platos (id_plato, nombre, categoria, precio) VALUES
('P001', 'Hamburguesa Clásica', 'PLATOS', 18.50),
('P002', 'Hamburguesa Royal', 'PLATOS', 22.00),
('P003', 'Lomo Saltado Criollo', 'PLATOS', 32.00),
('B001', 'Gaseosa 500ml', 'BEBIDAS', 5.00),
('B002', 'Chicha Morada Jarra 1L', 'BEBIDAS', 12.00);

-- 4. Tabla Principal de Pedidos
DROP TABLE IF EXISTS pedidos;
CREATE TABLE pedidos (
    id_pedido VARCHAR(30) PRIMARY KEY,
    cliente VARCHAR(120) NOT NULL,
    canal_venta VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'REGISTRADO',
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    recargo DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    notas TEXT,
    metodo_pago VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Tabla de Detalles del Pedido
DROP TABLE IF EXISTS detalles_pedido;
CREATE TABLE detalles_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido VARCHAR(30) NOT NULL,
    descripcion_item VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_pedido_detalle FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE
);

-- ====================================================================
-- PROCEDIMIENTOS ALMACENADOS (Stored Procedures)
-- ====================================================================

DELIMITER $$

-- SP 1: Registrar nuevo pedido
DROP PROCEDURE IF EXISTS sp_registrar_pedido$$
CREATE PROCEDURE sp_registrar_pedido(
    IN p_id VARCHAR(30),
    IN p_cliente VARCHAR(120),
    IN p_canal VARCHAR(20),
    IN p_estado VARCHAR(30),
    IN p_total DECIMAL(10,2),
    IN p_notas TEXT,
    IN p_metodo VARCHAR(50)
)
BEGIN
    INSERT INTO pedidos (id_pedido, cliente, canal_venta, estado, total, notas, metodo_pago)
    VALUES (p_id, p_cliente, p_canal, p_estado, p_total, p_notas, p_metodo);
END$$

-- SP 2: Avanzar o actualizar estado de pedido (Ciclo State)
DROP PROCEDURE IF EXISTS sp_actualizar_estado$$
CREATE PROCEDURE sp_actualizar_estado(
    IN p_id VARCHAR(30),
    IN p_nuevo_estado VARCHAR(30)
)
BEGIN
    UPDATE pedidos SET estado = p_nuevo_estado WHERE id_pedido = p_id;
END$$

-- SP 3: Descontar stock de almacén con validación
DROP PROCEDURE IF EXISTS sp_descontar_stock$$
CREATE PROCEDURE sp_descontar_stock(
    IN p_codigo VARCHAR(30),
    IN p_cantidad INT,
    OUT p_resultado INT
)
BEGIN
    DECLARE v_stock INT;
    SELECT stock_disponible INTO v_stock FROM insumos_almacen WHERE codigo_insumo = p_codigo;
    
    IF v_stock >= p_cantidad THEN
        UPDATE insumos_almacen 
        SET stock_disponible = stock_disponible - p_cantidad 
        WHERE codigo_insumo = p_codigo;
        SET p_resultado = 1; -- Exitoso
    ELSE
        SET p_resultado = 0; -- Stock insuficiente
    END IF;
END$$

DELIMITER ;
