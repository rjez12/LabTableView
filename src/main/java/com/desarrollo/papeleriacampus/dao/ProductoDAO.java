package com.desarrollo.papeleriacampus.dao;

import com.desarrollo.papeleriacampus.conexiondb.DatabaseConnection;
import com.desarrollo.papeleriacampus.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ProductoDAO {

    // Carga categorías con productos registrados.
    public List<String> listarCategorias() throws SQLException {
        String sql =
                "SELECT DISTINCT c.nombre "
                        + "FROM catalogos.categoria c "
                        + "INNER JOIN catalogos.producto p "
                        + "ON p.categoria = c.nombre "
                        + "ORDER BY c.nombre";

        List<String> categorias = new ArrayList<>();

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }

            return categorias;

        } catch (SQLException e) {
            registrarError("Cargar categorías", e);
            throw e;
        }
    }

    // Combina filtros manteniendo separados el SQL.
    public List<Producto> buscar(
            String texto, String categoria, boolean conStock
    ) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_producto, p.codigo, p.nombre, "
                        + "c.nombre AS categoria, p.precio, p.stock "
                        + "FROM catalogos.producto p "
                        + "INNER JOIN catalogos.categoria c "
                        + "ON p.categoria = c.nombre "
                        + "WHERE 1=1"
        );

        List<Object> parametros = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (p.nombre ILIKE ? OR p.codigo ILIKE ?)");
            parametros.add("%" + texto.trim() + "%");
            parametros.add("%" + texto.trim() + "%");
        }

        if (categoria != null && !"Todas".equals(categoria)) {
            sql.append(" AND c.nombre = ?");
            parametros.add(categoria);
        }

        if (conStock) {
            sql.append(" AND p.stock > 0");
        }

        sql.append(" ORDER BY p.codigo ASC");

        List<Producto> productos = new ArrayList<>();

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getBigDecimal("precio"),
                            rs.getInt("stock")
                    ));
                }
            }

            return productos;

        } catch (SQLException e) {
            registrarError("Buscar productos", e);
            throw e;
        }
    }

    // Excluye ID propio al bucar duplicados
    public boolean existeCodigo(
            String codigo, Integer excluirId
    ) throws SQLException {

        String sql =
                "SELECT 1 FROM catalogos.producto WHERE codigo = ?";

        if (excluirId != null) {
            sql += " AND id_producto <> ?";
        }

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            if (excluirId != null) {
                ps.setInt(2, excluirId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            registrarError("Comprobar código", e);
            throw e;
        }
    }

    private void asignar(
            PreparedStatement ps, Producto p
    ) throws SQLException {

        ps.setString(1, p.getCodigo());
        ps.setString(2, p.getNombre());
        ps.setString(3, p.getCategoria());
        ps.setBigDecimal(4, p.getPrecio());
        ps.setInt(5, p.getStock());
    }

    public int guardar(Producto p) throws SQLException {
        String sql =
                "INSERT INTO catalogos.producto "
                        + "(codigo, nombre, categoria, precio, stock) "
                        + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignar(ps, p);
            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("Guardar producto", e);
            throw e;
        }
    }

    public int actualizar(Producto p) throws SQLException {
        String sql =
                "UPDATE catalogos.producto "
                        + "SET codigo=?, nombre=?, categoria=?, precio=?, stock=? "
                        + "WHERE id_producto=?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignar(ps, p);
            ps.setInt(6, p.getIdProducto());

            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("Actualizar producto", e);
            throw e;
        }
    }

    public int eliminar(int idProducto) throws SQLException {
        String sql =
                "DELETE FROM catalogos.producto WHERE id_producto = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("Eliminar producto", e);
            throw e;
        }
    }

    private void registrarError(String operacion, SQLException e) {
        System.err.println(
                operacion
                        + " | SQLState=" + e.getSQLState()
                        + " | " + e.getMessage()
        );
    }
}