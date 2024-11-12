package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/ManualPaymentServlet")
public class ManualPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            BufferedReader reader = request.getReader();
            StringBuilder requestData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestData.append(line);
            }

            JSONObject jsonRequest = new JSONObject(requestData.toString());
            String phoneNumber = jsonRequest.optString("phone");
            String transactionId = jsonRequest.optString("transactionId");
            double amount = jsonRequest.optDouble("amount", -1);

            if (phoneNumber.isEmpty() || transactionId.isEmpty() || amount <= 0) {
                sendJsonResponse(response, false, "Invalid input data.");
                return;
            }

            // Validate phone number format
            if (!validatePhoneNumber(phoneNumber)) {
                sendJsonResponse(response, false, "Invalid phone number format.");
                return;
            }

            // Store manual payment
            storeManualPaymentData(phoneNumber, amount, transactionId);
            sendJsonResponse(response, true, "Manual payment recorded. Verification pending.");

        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "An internal server error occurred.");
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("2547[0-9]{8}");
    }

    private void storeManualPaymentData(String phoneNumber, double amount, String transactionId) {
        String query = "INSERT INTO payments (phone_number, amount, transaction_id, payment_status) VALUES (?, ?, ?, 'pending')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            stmt.setDouble(2, amount);
            stmt.setString(3, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error storing manual payment data for phone number: " + phoneNumber);
            e.printStackTrace();
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("success", success);
            json.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.getWriter().write(json.toString());
    }
}
