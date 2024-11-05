<%@ page import="java.sql.*" %>
<%@ page import="com.eliud.login.DatabaseConnection" %>

<%
    String username = request.getParameter("username");
    String phone = request.getParameter("Access Code");

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, phone);

        int rowsInserted = pstmt.executeUpdate();
        if (rowsInserted > 0) {
            out.println("<p>User registered successfully!</p>");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        out.println("<p>Database error: " + e.getMessage() + "</p>");
    } finally {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
%>
