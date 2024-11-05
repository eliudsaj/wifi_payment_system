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
     return DriverManager.getConnection(DB_URL, USER, PASS);
 }
}
