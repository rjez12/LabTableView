-- Creación de base de datos

CREATE DATABASE papeleria_campos;

-- Creación de esquemas y bases de datos

CREATE SCHEMA IF NOT EXISTS catalogos;

CREATE TABLE catalogos.producto (
    id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INTEGER NOT NULL,

    CONSTRAINT chk_producto_codigo CHECK (
        codigo = UPPER(BTRIM(codigo))
        AND CHAR_LENGTH(BTRIM(codigo)) BETWEEN 1 AND 10
    ),

    CONSTRAINT chk_producto_nombre CHECK (
        nombre = BTRIM(nombre)
        AND CHAR_LENGTH(nombre) BETWEEN 3 AND 80
    ),

    CONSTRAINT chk_producto_categoria CHECK (
        categoria IN ('Papelería', 'Tecnología', 'Accesorios')
    ),

    CONSTRAINT chk_producto_precio CHECK (
        precio > 0
    ),

    CONSTRAINT chk_producto_stock CHECK (
        stock >= 0
    )
);

SELECT current_database();

SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_name = 'producto';
	