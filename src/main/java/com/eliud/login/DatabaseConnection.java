/**
 * 
 */
/**
 * 
 */
package com.eliud.login;

//DatabaseConnection.java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:8090/wifi_payment_system";
    private static final String USER = "root";
    private static final String PASS = "developer";

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load and register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}