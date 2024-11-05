package com.send;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MpesaDataSender {
    // Replace with your database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:8090/wifi_payment_system"; // Change this to your DB URL
    private static final String USER = "root"; // Change this to your DB username
    private static final String PASS = "developer"; // Change this to your DB password

    public void sendMpesaData(String shortcode, String phone, String amount, String account, String remarks) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Establish the connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Prepare the SQL statement
            String sql = "INSERT INTO mpesa_table (shortcode, phone, amount, account, remarks) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // Set the parameters
            pstmt.setString(1, shortcode);
            pstmt.setString(2, phone);
            pstmt.setString(3, amount);
            pstmt.setString(4, account);
            pstmt.setString(5, remarks);

            // Execute the insert
            pstmt.executeUpdate();
            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
