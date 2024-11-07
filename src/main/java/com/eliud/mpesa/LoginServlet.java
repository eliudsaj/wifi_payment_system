package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessCode = request.getParameter("username");  // Access code entered by the user

        // Validate the access code
        if (isValidAccessCode(accessCode)) {
            // Code is valid, redirect to success page or logged-in area
            response.sendRedirect("Status.jsp");  // Change this to your welcome page or dashboard
        } else {
            // Code is invalid, redirect to login with an error message
            response.sendRedirect("error.jsp?error=Invalid Access Code");  // Display an error on the login page
        }
    }

    private boolean isValidAccessCode(String accessCode) {
        String query = "SELECT * FROM payments WHERE access_code = ? AND payment_status = 'Completed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accessCode);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // If a result is returned, the access code is valid
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
