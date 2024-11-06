package com.eliud.mpesa;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import java.io.IOException;

@WebServlet("/StatusServlet")
public class StatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");

        // Check if message is null or empty and set a default message if necessary
        if (message == null || message.isEmpty()) {
            message = "No status message provided.";
        }

        // Log the message for debugging purposes
        System.out.println("Received status message: " + message);

        // Forward message to JSP for display
        request.setAttribute("statusMessage", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("status.jsp");
        dispatcher.forward(request, response);
    }
}

