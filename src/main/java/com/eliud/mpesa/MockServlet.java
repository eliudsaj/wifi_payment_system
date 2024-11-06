package com.eliud.mpesa;


import com.eliud.login.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

@WebServlet("/MockServlet")
public class MockServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phoneNumber = request.getParameter("phone");
        String amount = request.getParameter("amount");

        // Initiate M-Pesa payment request logic here
        String transactionId = initiateMpesaPayment(phoneNumber, amount);

        if (transactionId != null) {
            // Directly using DatabaseConnection's getConnection() method
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO payments (phone_number, transaction_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, phoneNumber);
                stmt.setString(2, transactionId);
                stmt.setString(3, amount);
                stmt.setString(4, "PENDING"); // Initial status is "PENDING"
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();

                response.setContentType("text/plain");
                response.getWriter().write("Payment initiated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Payment initiation failed.");
        }
    }

    // Mocked M-Pesa request logic (to be replaced with actual integration)
    private String initiateMpesaPayment(String phoneNumber, String amount) {
        // Simulate successful transaction ID generation
        return "TXN123456"; // Simulated transaction ID for the payment
    }
}
