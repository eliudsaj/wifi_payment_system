package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CONSUMER_KEY = "your_consumer_key";
    private static final String CONSUMER_SECRET = "your_consumer_secret";
    private static final String SHORTCODE = "195897";
    private static final String PASSKEY = "your_passkey";
    private static final String CALLBACK_URL = "http://localhost:8080/yourapp/PaymentCallbackServlet"; // Update with correct path

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        try {
            JSONObject requestData = new JSONObject(stringBuilder.toString());
            String phoneNumber = requestData.getString("phone");
            int amount = requestData.getInt("amount");

            if (!validatePhoneNumber(phoneNumber)) {
                sendJsonResponse(response, false, "Invalid phone number format.");
                return;
            }

            String accessToken = getAccessToken();
            if (accessToken == null) {
                sendJsonResponse(response, false, "Failed to get access token.");
                return;
            }

            boolean paymentInitiated = initiateStkPush(phoneNumber, amount, accessToken);
            if (paymentInitiated) {
                String accessCode = generateAccessCode();
                storeInitialPaymentData(phoneNumber, amount, accessCode); // Store data before callback
                sendJsonResponse(response, true, "Payment initiated! Please check your phone.");
            } else {
                sendJsonResponse(response, false, "Payment initiation failed. You can proceed manually.");
            }
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(response, false, "Invalid JSON data.");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(response, false, "An error occurred during payment initiation.");
            e.printStackTrace();
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("2547[0-9]{8}");
    }

    private String getAccessToken() {
        try {
            String authUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
            String authString = CONSUMER_KEY + ":" + CONSUMER_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

            URL url = new URL(authUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject json = new JSONObject(response.toString());
                    return json.getString("access_token");
                }
            } else {
                System.err.println("Failed to get access token. Response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Error getting access token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private boolean initiateStkPush(String phoneNumber, int amount, String accessToken) {
        try {
            String stkUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String password = Base64.getEncoder().encodeToString((SHORTCODE + PASSKEY + timestamp).getBytes(StandardCharsets.UTF_8));

            JSONObject json = new JSONObject();
            json.put("BusinessShortCode", SHORTCODE);
            json.put("Password", password);
            json.put("Timestamp", timestamp);
            json.put("TransactionType", "CustomerPayBillOnline");
            json.put("Amount", amount);
            json.put("PartyA", phoneNumber);
            json.put("PartyB", SHORTCODE);
            json.put("PhoneNumber", phoneNumber);
            json.put("CallBackURL", CALLBACK_URL);
            json.put("AccountReference", "Ref001");
            json.put("TransactionDesc", "Payment");

            URL url = new URL(stkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            System.err.println("Error initiating STK push: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String generateAccessCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // Generate a 6-digit code
        return String.valueOf(code);
    }

    private void storeInitialPaymentData(String phoneNumber, int amount, String accessCode) {
        String query = "INSERT INTO payments (phone_number, amount, access_code, payment_status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            stmt.setInt(2, amount);
            stmt.setString(3, accessCode);
            stmt.setString(4, "Pending");
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error storing initial payment data for phone number: " + phoneNumber);
            e.printStackTrace();
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);
        if (!success) {
            jsonResponse.put("redirectUrl", "Payment.jsp");  // Add redirect URL for failed payments
        }
        response.getWriter().write(jsonResponse.toString());
    }
}
