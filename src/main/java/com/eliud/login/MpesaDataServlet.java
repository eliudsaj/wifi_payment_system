package com.eliud.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;  // Import Gson library for JSON parsing
import com.google.gson.JsonParser;

@WebServlet("/MpesaData")
public class MpesaDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Read the JSON data from the request
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        // Parse JSON data
        String mpesaDataJson = sb.toString();
        JsonObject mpesaData = JsonParser.parseString(mpesaDataJson).getAsJsonObject();

        // Extract fields from the parsed JSON
        String shortcode = mpesaData.get("shortcode").getAsString();
        String phone = mpesaData.get("phone").getAsString();
        double amount = mpesaData.get("amount").getAsDouble();
        String account = mpesaData.get("account").getAsString();
        String remarks = mpesaData.get("Remarks").getAsString();

        // Prepare to insert data into the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO mpesa_transactions (shortcode, phone, amount, account, remarks) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set the parameters for the prepared statement
            pstmt.setString(1, shortcode);
            pstmt.setString(2, phone);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, account);
            pstmt.setString(5, remarks);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                out.write("{\"success\": true}");
            } else {
                out.write("{\"success\": false, \"error\": \"Failed to save data\"}");
            }
        } catch (SQLException e) {
            out.write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        } finally {
            out.close(); // Ensure the PrintWriter is closed
        }
    }
}
