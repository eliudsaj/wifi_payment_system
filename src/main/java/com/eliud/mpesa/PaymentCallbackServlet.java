package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/PaymentCallbackServlet")
public class PaymentCallbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        try {
            JSONObject callbackData = new JSONObject(stringBuilder.toString());
            String phoneNumber = callbackData.getString("phoneNumber");
            String status = callbackData.getString("status");
            int amount = callbackData.getInt("amount");

            // Update payment status in the database
            updatePaymentStatus(phoneNumber, status);

            if ("success".equalsIgnoreCase(status)) {
                // Generate access code and send SMS
                String accessCode = generateAccessCode();
                String limit = calculateLimitFromDatabase(amount);
                storePaymentData(phoneNumber, amount, accessCode, limit);

                // Send SMS with access code
                SmsSender.sendSms(phoneNumber, "Your access code is: " + accessCode);
            }

            // Send success response back to Safaricom
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Callback received and processed successfully.");

        } catch (JSONException e) {
            // JSON parsing error
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JSON data received.");
            e.printStackTrace();
        } catch (Exception e) {
            // Other errors
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred processing the callback.");
            e.printStackTrace();
        }
    }

    // Update payment status in the database
    private void updatePaymentStatus(String phoneNumber, String status) {
        String query = "UPDATE payments SET payment_status = ? WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, phoneNumber);
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                System.err.println("No rows updated for phone number: " + phoneNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error updating payment status for phone number: " + phoneNumber);
            e.printStackTrace();
        }
    }

    // Generate a random access code for the user
    private String generateAccessCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // Generate a 6-digit code
        return String.valueOf(code);
    }

    // Calculate the limit based on the amount from the database
    private String calculateLimitFromDatabase(int amount) {
        String limit = "Default Limit";
        String query = "SELECT duration_limit FROM pricing_limits WHERE price = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, amount);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                limit = rs.getString("duration_limit");
            } else {
                System.err.println("No limit found for amount: " + amount);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating limit for amount: " + amount);
            e.printStackTrace();
        }

        return limit;
    }

    // Store payment data with access code and limit in the database
    private void storePaymentData(String phoneNumber, int amount, String accessCode, String limit) {
        String query = "UPDATE payments SET access_code = ?, limit = ?, payment_status = ? WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accessCode);
            stmt.setString(2, limit);
            stmt.setString(3, "Completed");  // Mark the payment as completed
            stmt.setString(4, phoneNumber);
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                System.err.println("No rows updated for phone number: " + phoneNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error storing payment data for phone number: " + phoneNumber);
            e.printStackTrace();
        }
    }
}
