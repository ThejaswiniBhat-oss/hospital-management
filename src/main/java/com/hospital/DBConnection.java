package com.hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the JDBC connection to the MySQL database.
 *
 * VIVA POINT: This is the heart of "database connectivity".
 * DriverManager.getConnection() uses the JDBC URL, username and password
 * to open a connection to the MySQL server. Every CRUD operation in this
 * project opens a connection through this single class.
 */
public class DBConnection {

    // If an environment variable is set we use it; otherwise we fall back to
    // the default local values. This lets the SAME code run in Codespaces
    // and on a laptop with XAMPP, without editing anything.
    private static final String URL =
            getEnvOrDefault("DB_URL", "jdbc:mysql://127.0.0.1:3306/hospital");
    private static final String USER =
            getEnvOrDefault("DB_USER", "root");
    private static final String PASSWORD =
            getEnvOrDefault("DB_PASSWORD", "root");

    private static String getEnvOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /**
     * Opens and returns a new connection to the hospital database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
