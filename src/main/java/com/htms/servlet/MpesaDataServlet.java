package com.htms.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/MpesaData")
public class MpesaDataServlet extends HttpServlet {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/yourDatabase"; // Replace with your database URL
    private static final String DB_USER = "yourUsername"; // Replace with your database username
    private static final String DB_PASSWORD = "yourPassword"; // Replace with your database password

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String shortcode = request.getParameter("shortcode");
        String phone = request.getParameter("phone");
        String amount = request.getParameter("amount");
        String account = request.getParameter("account");
        String remarks = request.getParameter("remarks");

        // Validate parameters (add your own validation logic)
        if (shortcode == null || phone == null || amount == null || account == null || remarks == null) {
            out.print("{\"code\":1, \"msg\":\"Invalid input data\"}");
            return;
        }

        // Database insertion logic
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO mpesa_data (shortcode, phone, amount, account, remarks) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, shortcode);
                statement.setString(2, phone);
                statement.setString(3, amount);
                statement.setString(4, account);
                statement.setString(5, remarks);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    out.print("{\"code\":0, \"msg\":\"Data saved successfully\"}");
                } else {
                    out.print("{\"code\":2, \"msg\":\"Failed to save data\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"code\":3, \"msg\":\"Database error: " + e.getMessage() + "\"}");
        }
    }
}
