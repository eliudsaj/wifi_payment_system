package com.eliud.mpesa;

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
import org.json.JSONObject;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CONSUMER_KEY = "rdGcLVrAS7DxXGIAu2twbki8x2DEnY484ZXp09qSc3oo8FUG";
    private static final String CONSUMER_SECRET = "BYdQQrGfElR83M8Cv88AYAvy7nrZTN65vqbG1SBA5xAUxXkdedOnYznKK2ZA6QGm";
    private static final String SHORTCODE = "195897";
    private static final String PASSKEY = "your_passkey";
    private static final String CALLBACK_URL = "http://localhost:8080/htpms/LoginServlet"; // Change to your actual callback URL

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Step 1: Retrieve JSON data sent from the frontend
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        
        JSONObject requestData = new JSONObject(stringBuilder.toString());
        String phoneNumber = requestData.getString("phone");
        String amount = requestData.getString("amount");
        String accountReference = requestData.getString("account");

        // Step 2: Get access token
        String accessToken = getAccessToken();
        if (accessToken == null) {
            sendJsonResponse(response, false, "Failed to get access token.");
            return;
        }

        // Step 3: Process STK push request
        boolean paymentSuccessful = initiateStkPush(phoneNumber, amount, accessToken);
        if (paymentSuccessful) {
            sendJsonResponse(response, true, "Payment request sent successfully!");
        } else {
            sendJsonResponse(response, false, "Payment request failed.");
        }
    }

    // Method to get access token
    private String getAccessToken() {
        try {
            String authUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
            String authString = CONSUMER_KEY + ":" + CONSUMER_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

            URL url = new URL(authUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            if (conn.getResponseCode() == 200) {
                // Use BufferedReader to read the response
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    // Parse the JSON response to get the access token
                    JSONObject json = new JSONObject(response.toString());
                    return json.getString("access_token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to initiate the STK push request
    private boolean initiateStkPush(String phoneNumber, String amount, String accessToken) {
        try {
            String stkUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
            String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            String password = Base64.getEncoder().encodeToString((SHORTCODE + PASSKEY + timestamp).getBytes());

            // Build JSON payload for STK push request
            JSONObject json = new JSONObject();
            json.put("BusinessShortCode", SHORTCODE);
            json.put("Password", password);
            json.put("Timestamp", timestamp);
            json.put("TransactionType", "CustomerPayBillOnline");
            json.put("Amount", Integer.parseInt(amount));
            json.put("PartyA", phoneNumber);
            json.put("PartyB", SHORTCODE);
            json.put("PhoneNumber", phoneNumber);
            json.put("CallBackURL", CALLBACK_URL);
            json.put("AccountReference", "Ref001");
            json.put("TransactionDesc", "Payment");

            // Set up HTTP connection
            URL url = new URL(stkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send JSON request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            return conn.getResponseCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to send JSON response back to the frontend
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);
        response.getWriter().write(jsonResponse.toString());
    }
}
