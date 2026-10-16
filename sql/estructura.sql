-- Creación de base de datos

CREATE DATABASE papeleria_campus

-- Creación de esquema y de tabla junto con constraints
CREATE SCHEMA IF NOT EXISTS catalogos;

CREATE TABLE catalogos.categoria (
    nombre VARCHAR(20) PRIMARY KEY,

    CONSTRAINT chk_categoria_nombre CHECK (
        nombre IN ('Papelería', 'Tecnología', 'Accesorios')
    )
);

CREATE TABLE catalogos.producto (
    id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INTEGER NOT NULL,

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria)
        REFERENCES catalogos.categoria(nombre),

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