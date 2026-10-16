package com.desarrollo.papeleriacampus.conexiondb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        String url = obtenerVariable("DB_URL");
        String usuario = obtenerVariable("DB_USER");
        String password = obtenerVariable("DB_PASSWORD");

        return DriverManager.getConnection(url, usuario, password);
    }

    // Comprueba que la configuración esté disponible.
    private static String obtenerVariable(String nombre)
            throws SQLException {

        String valor = System.getenv(nombre);

        if (valor == null || valor.isBlank()) {
            throw new SQLException(
                    "Falta configurar la variable de entorno " + nombre,
                    "08001"
            );
        }

        return valor;
    }
}