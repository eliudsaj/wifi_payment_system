package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transactionCode = request.getParameter("username");

        // Use the DatabaseConnection class to get a connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Prepare SQL query to check payment status using the transaction code
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT payment_status FROM payments WHERE phone_number = ?");
            stmt.setString(1, transactionCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && "completed".equals(rs.getString("payment_status"))) {
                response.getWriter().write("Access granted. Welcome!");
                request.getRequestDispatcher("status.jsp").forward(request, response);
            } else {
                response.getWriter().write("Payment not completed or access code invalid.");
            }
        } catch (SQLException e) {
            // Print stack trace to the log and return detailed error response
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing login: " + e.getMessage());
        }
    }
}
