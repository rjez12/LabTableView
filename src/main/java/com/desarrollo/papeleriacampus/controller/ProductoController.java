package com.desarrollo.papeleriacampus.controller;

import com.desarrollo.papeleriacampus.dao.ProductoDAO;
import com.desarrollo.papeleriacampus.model.Producto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {

    @FXML
    private TextField txtCodigo, txtNombre, txtPrecio,
            txtStock, txtBusqueda;

    @FXML
    private ComboBox<String> cmbCategoria, cmbFiltroCategoria;

    @FXML
    private Button btnNuevo, btnGuardar, btnActualizar,
            btnEliminar, btnBuscar, btnLimpiarFiltros;

    @FXML
    private CheckBox chkExistencias;

    @FXML
    private Label lblContador;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, Integer> colId, colStock;

    @FXML
    private TableColumn<Producto, String> colCodigo,
            colNombre, colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    private final ProductoDAO dao = new ProductoDAO();

    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTableView();
        configurarSeleccionTableView();

        btnNuevo.setOnAction(event -> limpiarFormulario());
        btnGuardar.setOnAction(event -> guardarProducto());
        btnActualizar.setOnAction(event -> actualizarProducto());
        btnEliminar.setOnAction(event -> eliminarProducto());
        btnBuscar.setOnAction(event -> buscarProductos());
        btnLimpiarFiltros.setOnAction(event -> limpiarFiltros());
        txtBusqueda.setOnAction(event -> buscarProductos());

        cmbCategoria.setEditable(false);
        cmbFiltroCategoria.setEditable(false);
        cmbFiltroCategoria.setValue("Todas");

        cargarProductos();
    }

    private boolean cargarCategorias() {
        String categoriaActual = cmbCategoria.getValue();
        String filtroActual = cmbFiltroCategoria.getValue();

        try {
            List<String> categorias = dao.listarCategorias();

            cmbCategoria.setItems(
                    FXCollections.observableArrayList(categorias)
            );

            cmbCategoria.setValue(
                    categorias.contains(categoriaActual)
                            ? categoriaActual : null
            );

            ObservableList<String> filtros =
                    FXCollections.observableArrayList(categorias);

            filtros.add(0, "Todas");
            cmbFiltroCategoria.setItems(filtros);

            cmbFiltroCategoria.setValue(
                    filtroActual == null ? "Todas" : filtroActual
            );

            return true;

        } catch (SQLException e) {
            lblContador.setText(
                    "No se pudieron cargar las categorías."
            );

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las categorías.", e
            );

            return false;
        }
    }

    private void configurarTableView() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getIdProducto()
                ).asObject());

        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                ));

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                ));

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                ));

        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getPrecio()
                ));

        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject());

        tblProductos.setItems(listaProductos);

        colId.setPrefWidth(45);
        colCodigo.setPrefWidth(75);
        colNombre.setPrefWidth(210);
        colCategoria.setPrefWidth(125);
        colPrecio.setPrefWidth(100);
        colStock.setPrefWidth(65);

        tblProductos.setPrefHeight(190);
        tblProductos.setMinHeight(160);

        tblProductos.setPlaceholder(
                new Label("Sin productos para mostrar.")
        );
    }

    private void configurarSeleccionTableView() {
        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarProductoEnFormulario(seleccionado);
                    }
                });
    }

    private void cargarProductoEnFormulario(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtPrecio.setText(p.getPrecio().toPlainString());
        txtStock.setText(String.valueOf(p.getStock()));
    }

    // Valida los datos.
    private boolean validarFormulario() {
        txtCodigo.setText(txtCodigo.getText().trim().toUpperCase());
        txtNombre.setText(txtNombre.getText().trim());

        if (txtCodigo.getText().isEmpty()
                || txtCodigo.getText().length() > 10) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código inválido",
                    "El código debe tener de 1 a 10 caracteres."
            );

            txtCodigo.requestFocus();
            return false;
        }

        if (txtNombre.getText().length() < 3
                || txtNombre.getText().length() > 80) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nombre inválido",
                    "El nombre debe tener de 3 a 80 caracteres."
            );

            txtNombre.requestFocus();
            return false;
        }

        String categoria = cmbCategoria.getValue();

        if (categoria == null
                || !cmbCategoria.getItems().contains(categoria)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Categoría requerida",
                    "Selecciona una categoría del catálogo."
            );

            return false;
        }

        try {
            BigDecimal precio =
                    new BigDecimal(txtPrecio.getText().trim());

            if (precio.compareTo(new BigDecimal("0.01")) < 0
                    || precio.compareTo(
                    new BigDecimal("99999999.99")
            ) > 0
                    || precio.scale() > 2) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "El precio debe ser de 0.01 a 99999999.99, "
                                + "con hasta dos decimales."
                );

                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "Ingresa un precio numérico usando punto decimal."
            );

            return false;
        }

        try {
            if (Integer.parseInt(txtStock.getText().trim()) < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock inválido",
                        "El stock no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Stock inválido",
                    "El stock debe ser un entero de 0 a 2147483647."
            );

            return false;
        }

        return true;
    }

    private Producto productoDelFormulario(int id) {
        return new Producto(
                id,
                txtCodigo.getText(),
                txtNombre.getText(),
                cmbCategoria.getValue(),
                new BigDecimal(txtPrecio.getText().trim()),
                Integer.parseInt(txtStock.getText().trim())
        );
    }

    @FXML
    public void guardarProducto() {
        if (!validarFormulario()) return;

        try {
            if (dao.existeCodigo(txtCodigo.getText(), null)) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Otro producto ya utiliza ese código."
                );

                return;
            }

            int filas = dao.guardar(productoDelFormulario(0));

            informarResultado(
                    filas, "Producto registrado correctamente."
            );

            if (filas == 1) limpiarFormulario();

            cargarProductos();

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudo guardar el producto.", e
            );
        }
    }

    @FXML
    public void actualizarProducto() {
        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Selecciona un producto para actualizar."
            );

            return;
        }

        if (!validarFormulario()) return;

        try {
            if (dao.existeCodigo(
                    txtCodigo.getText(),
                    seleccionado.getIdProducto()
            )) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Otro producto ya utiliza ese código."
                );

                return;
            }

            int filas = dao.actualizar(
                    productoDelFormulario(
                            seleccionado.getIdProducto()
                    )
            );

            informarResultado(
                    filas, "Producto actualizado correctamente."
            );

            limpiarFormulario();
            cargarProductos();

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudo actualizar el producto.", e
            );
        }
    }

    @FXML
    public void eliminarProducto() {
        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Selecciona un producto para eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Eliminar permanentemente "
                        + seleccionado.getNombre() + "?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        try {
            int filas = dao.eliminar(
                    seleccionado.getIdProducto()
            );

            informarResultado(
                    filas, "Producto eliminado correctamente."
            );

            limpiarFormulario();
            cargarProductos();

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudo eliminar el producto.", e
            );
        }
    }

    private void informarResultado(int filas, String mensaje) {
        if (filas == 1) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Operación completada",
                    mensaje
            );

        } else {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin cambios",
                    "No se modificó ninguna fila. El producto puede "
                            + "haber sido eliminado; el listado se recargará."
            );
        }
    }

    @FXML
    public void buscarProductos() {
        consultar(true);
    }

    @FXML
    public void cargarProductos() {
        if (cargarCategorias()) {
            consultar(false);
        }
    }

    // Conserva filtros después de accionar un boton.
    private void consultar(boolean avisarSinResultados) {
        try {
            List<Producto> resultados = dao.buscar(
                    txtBusqueda.getText().trim(),
                    cmbFiltroCategoria.getValue(),
                    chkExistencias.isSelected()
            );

            listaProductos.setAll(resultados);

            lblContador.setText(
                    "Registros visibles: " + listaProductos.size()
            );

            if (avisarSinResultados && resultados.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin coincidencias",
                        "No existen productos que coincidan "
                                + "con los filtros."
                );
            }

        } catch (SQLException e) {
            lblContador.setText(
                    "Error al recargar. Último listado: "
                            + listaProductos.size()
            );

            mostrarErrorBaseDatos(
                    "No se pudieron consultar los productos.", e
            );
        }
    }

    @FXML
    public void limpiarFormulario() {
        tblProductos.getSelectionModel().clearSelection();

        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();

        txtCodigo.requestFocus();
    }

    @FXML
    public void limpiarFiltros() {
        txtBusqueda.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkExistencias.setSelected(false);
        cargarProductos();
    }

    private void mostrarErrorBaseDatos(
            String operacion, SQLException e
    ) {
        String estado =
                e.getSQLState() == null ? "" : e.getSQLState();

        String mensaje = switch (estado) {
            case "23505" ->
                    "El código ya pertenece a otro producto.";

            case "23503" ->
                    "La categoría ya no está disponible. "
                            + "Pulsa Recargar.";

            case "23514", "23502", "22003", "22001" ->
                    "Los datos incumplen una restricción "
                            + "de la base de datos.";

            case "28P01", "28000" ->
                    "Revisa DB_USER y DB_PASSWORD "
                            + "en la configuración de ejecución.";

            case "08001", "08006" ->
                    "Revisa DB_URL, DB_USER y DB_PASSWORD, "
                            + "y que PostgreSQL esté disponible.";

            case "3D000", "42P01" ->
                    "Comprueba la base indicada en DB_URL "
                            + "y las tablas del esquema catalogos.";

            default ->
                    "No se pudo completar la operación. "
                            + "Revisa la consola para conocer el detalle.";
        };

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de base de datos",
                operacion + "\n" + mensaje
        );
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}