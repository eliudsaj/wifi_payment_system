package com.eliud.mpesa;

import com.eliud.login.DatabaseConnection;  // Assuming this is where your database connection class is located


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Authentication logic
        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            // If authenticated, redirect to home page
            response.sendRedirect("home.jsp");
        } else {
            // If authentication fails, redirect with error message
            response.sendRedirect("error.jsp?message=Invalid credentials");
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Database connection setup
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Establish connection
            conn = DatabaseConnection.getConnection();  // Use your existing DatabaseConnection class here
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

            // Use prepared statement to avoid SQL injection
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);  // In a real-world scenario, never store passwords in plaintext. Use hashing!

            // Execute query
            rs = stmt.executeQuery();

            // If we find a matching username/password, authentication is successful
            if (rs.next()) {
                return true; // User found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return false if no matching user is found
        return false;
    }
}
