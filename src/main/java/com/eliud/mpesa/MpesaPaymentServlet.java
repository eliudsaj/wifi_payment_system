/**
 * 
 */
/**
 * 
 */
package com.eliud.mpesa;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/MpesaPaymentServlet")
public class MpesaPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        String amount = request.getParameter("amount");
        
        // Call to the M-Pesa API (replace with real implementation)
        boolean paymentSuccess = initiateMpesaPayment(phone, amount);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        if (paymentSuccess) {
            out.write("{ \"message\": \"Payment initiated successfully.\" }");
        } else {
            out.write("{ \"message\": \"Payment initiation failed.\" }");
        }
        out.flush();
    }

    private boolean initiateMpesaPayment(String phone, String amount) {
        // Here you would implement the M-Pesa API call with your API key and secret.
        // For demonstration purposes, this is a placeholder method.
        
        return true;  // Replace with real API call and response handling.
    }
}
