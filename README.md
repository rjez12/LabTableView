<div align="center">

# UNIVERSIDAD AMERICANA

### Facultad de Ingeniería y Arquitectura

---

<img src="docs/uam-logo.png" alt="Escudo de la Universidad Americana" width="220">

### Programación de Aplicaciones de Escritorio — Grupo 3

**Evaluación 1 — II Corte**

---

**Estudiante:**  
Jesser Jadiel Rodríguez Chavarría

**CIF:**  
25011185

**Docente:**  
Freddy Luis López Barrios

**Fecha:**  
15 de octubre de 2026

---

# Papelería Campus

### Práctica CRUD y búsqueda parametrizada

</div>

## Descripción

Aplicación de escritorio para administrar los productos de Papelería Campus. Permite registrar, consultar, actualizar y eliminar productos almacenados en PostgreSQL.

También permite buscar por código o nombre y combinar filtros por categoría y existencias.

## Herramientas utilizadas

| Herramienta | Versión |
|---|---|
| Java JDK | 21.0.12 |
| JavaFX | 21.0.6 |
| PostgreSQL | 18 |
| PostgreSQL JDBC | 42.7.14 |

El proyecto utiliza Maven y fue desarrollado en IntelliJ IDEA.

## Funciones

- Listado de productos ordenados por código.
- Contador de registros visibles.
- Registro y actualización de productos.
- Eliminación con confirmación.
- Búsqueda parcial por código o nombre.
- Filtros por categoría y productos con existencias.
- Validación de campos, precios, stock y códigos repetidos.
- Recarga del listado desde PostgreSQL.

## Base de datos

La aplicación utiliza la base `papeleria_campus`, el esquema `catalogos` y la tabla `producto`.

### Preparación

1. Abre pgAdmin y conecta con PostgreSQL.
2. Desde la base `postgres`, ejecuta por separado:

```sql
CREATE DATABASE papeleria_campus;
```

3. Abre Query Tool conectado a `papeleria_campus`.
4. Ejecuta primero [sql/estructura.sql](sql/estructura.sql).
5. Ejecuta después [sql/datos.sql](sql/datos.sql).

`estructura.sql` crea el esquema, la tabla y sus restricciones. `datos.sql` agrega los 12 productos iniciales y debe ejecutarse una sola vez sobre la tabla vacía.

Para comprobar la carga:

```sql
SELECT *
FROM catalogos.producto
ORDER BY codigo ASC;

SELECT COUNT(*) AS total_productos
FROM catalogos.producto;
```

El resultado debe contener 12 productos.

Para repetir las pruebas se conservan los registros iniciales y se retiran únicamente los productos temporales utilizados.

## Configuración de conexión

La aplicación obtiene los datos de conexión mediante variables de entorno.

En IntelliJ IDEA:

1. Abre Run → Edit Configurations.
2. Selecciona ProductoApplication.
3. En Environment variables, configura:

| Variable | Valor de ejemplo |
|---|---|
| DB_URL | jdbc:postgresql://localhost:5432/papeleria_campus |
| DB_USER | postgres |
| DB_PASSWORD | TU_CONTRASEÑA_LOCAL |

Coloca la contraseña real únicamente en la configuración local y deja desmarcada la opción Store as project file.

Si ejecutas Launcher u otra clase con una configuración diferente, agrega también allí las variables.

No se necesita un archivo `.env` ni dependencias adicionales.

## Categorías y consultas

Las categorías se almacenan en `catalogos.categoria` y se relacionan con `catalogos.producto` mediante el nombre de categoría.

El DAO utiliza INNER JOIN para consultar los productos y cargar las opciones de ambos ComboBox. Se muestran las categorías que tienen productos asociados. La opción Todas permite quitar el filtro por categoría.

### Instalación nueva

1. Crea la base `papeleria_campus`.
2. Ejecuta `sql/estructura.sql` conectado a esa base.
3. Ejecuta `sql/datos.sql` una sola vez sobre las tablas vacías.

### Actualización de la versión anterior

Si la tabla de productos ya existe y contiene los registros, ejecuta solamente `sql/actualizacion_categorias.sql` una vez. Este script agrega el catálogo y su relación sin eliminar los productos.

## Ejecución

1. Abre la carpeta del proyecto en IntelliJ IDEA.
2. Configura el JDK 21.
3. Carga las dependencias del archivo `pom.xml` desde Maven.
4. Comprueba que PostgreSQL esté funcionando y que la conexión esté configurada.
5. Ejecuta `ProductoApplication` o `Launcher`.

Al abrir la aplicación deben aparecer los 12 productos iniciales.

## Organización del proyecto

```text
PapeleriaCampus/
├── docs/
│   └── logo-uam.png
├── evidencias/
├── sql/
│   ├── estructura.sql
│   └── datos.sql
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/desarrollo/papeleriacampus/
│       │       ├── ProductoApplication.java
│       │       ├── Launcher.java
│       │       ├── conexiondb/
│       │       │   └── DatabaseConnection.java
│       │       ├── controller/
│       │       │   └── ProductoController.java
│       │       ├── dao/
│       │       │   └── ProductoDAO.java
│       │       └── model/
│       │           └── Producto.java
│       └── resources/
│           └── com/desarrollo/papeleriacampus/
│               ├── producto-view.fxml
│               └── estilos.css
├── pom.xml
├── README.md
└── pruebas.md
```

## Funcionamiento

El usuario realiza una acción en la interfaz y el controlador revisa los datos. Después, el DAO ejecuta la consulta u operación en PostgreSQL mediante la clase de conexión.

Los resultados se convierten en objetos `Producto` y se muestran en el TableView. Después de guardar, actualizar o eliminar, se consulta nuevamente la base y se conservan los filtros seleccionados.

```text
Interfaz → Controlador → DAO → PostgreSQL
                              ↓
TableView ← ObservableList ← Producto
```

Las consultas utilizan `PreparedStatement`. Las conexiones, sentencias y resultados se cierran mediante `try-with-resources`.

## Pruebas y evidencias

Los casos de búsqueda, CRUD, validaciones y manejo de errores se encuentran en [pruebas.md](pruebas.md).

Las capturas están en la carpeta [evidencias](evidencias/).

### Listado de productos

![Listado inicial de productos](evidencias/listado_inicial.png)

## Referencias técnicas

1. [pgJDBC: consultas, parámetros y ResultSet](https://jdbc.postgresql.org/documentation/query/)
2. [PostgreSQL: coincidencia de patrones e ILIKE](https://www.postgresql.org/docs/18/functions-matching.html)
3. [OpenJFX: TableView y su lista de elementos](https://openjfx.io/javadoc/21/javafx.controls/javafx/scene/control/TableView.html)
4. Oracle: [manejo de SQLException](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html) y [cierre de recursos](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html).
5. [PostgreSQL: restricciones de integridad](https://www.postgresql.org/docs/18/ddl-constraints.html)

## Material de apoyo

Se tomaron como referencia los ejemplos proporcionados por el docente y la documentación técnica indicada en el laboratorio. Se utilizó ChatGPT como apoyo para revisar código, resolver errores y organizar la documentación.