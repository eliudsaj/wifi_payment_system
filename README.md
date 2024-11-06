M-Pesa Payment Integration
This project demonstrates how to integrate M-Pesa STK push for online payments in a web application using Java Servlets and frontend JavaScript. It includes backend logic to authenticate with M-Pesa and initiate STK push payments, as well as a frontend interface for users to select a payment amount and phone number.

Table of Contents
Technologies Used
Setup
Backend Configuration
Frontend Configuration
How It Works
Endpoints
Running the Application
License
Technologies Used
Java - Backend programming language.
Servlets - For handling HTTP requests and responses.
HTML/CSS/JavaScript - Frontend technologies for creating the user interface.
M-Pesa API - For payment integration via STK push.
Tomcat - Web server to run the Java web application.
JSON - For handling data interchange between the frontend and backend.
Setup
Backend Configuration
Clone the repository or create a new Java web project.

Install and configure Tomcat in your development environment (Eclipse or another IDE).

M-Pesa Developer Credentials: You will need to sign up for a developer account with M-Pesa (Safaricom) and get the following credentials:

Consumer Key: For API authentication.
Consumer Secret: For API authentication.
Shortcode: The M-Pesa shortcode to handle payments.
Passkey: A secret passkey used in the M-Pesa payment process.
Callback URL: URL to which M-Pesa will send payment status updates.
Update PaymentServlet.java: Replace the placeholders in the PaymentServlet.java file:

java
Copy code
private static final String CONSUMER_KEY = "your_consumer_key";
private static final String CONSUMER_SECRET = "your_consumer_secret";
private static final String SHORTCODE = "your_shortcode";
private static final String PASSKEY = "your_passkey";
private static final String CALLBACK_URL = "http://localhost:8080/yourapp/LoginServlet";
Configure your server: Make sure the server is set to run on port 8080 (or update the port to match your configuration). Ensure web.xml is correctly configured for your servlet mappings.

Frontend Configuration
HTML & JavaScript:

The frontend uses fetch() to send the payment data (phone number and amount) to the backend servlet (PaymentServlet).
Ensure that the frontend has a file prices.json for loading the available data plan options.
Example prices.json format:

json
Copy code
{
  "server": "your_server_url",
  "shortcode": "your_shortcode",
  "quota": [
    { "price": 100, "color": "blue", "quota_limit": "500MB" },
    { "price": 200, "color": "green", "quota_limit": "1GB" }
  ],
  "duration": [
    { "price": 50, "color": "red", "quota_limit": "1 Hour" },
    { "price": 150, "color": "yellow", "quota_limit": "24 Hours" }
  ]
}
Frontend Validation:

The script validates the phone number format (12 digits) and ensures the user selects an amount before proceeding to payment.
Integration with STK Push:

When the user submits the payment form, the frontend sends a POST request to PaymentServlet with the phone number and amount.
The backend authenticates with M-Pesa, processes the STK push request, and the user is redirected to M-Pesa's payment page.
How It Works
User selects a data plan or time limit from the UI.
User enters their phone number (formatted as 2547xxx).
When the user clicks the "Buy" button:
The frontend validates the phone number and the selected amount.
The frontend sends the payment details to the PaymentServlet.
Backend (PaymentServlet):
The backend retrieves an access token from M-Pesa.
It sends an STK push request to M-Pesa using the provided shortcode, passkey, and other parameters.
M-Pesa initiates the payment and the user is prompted to complete the payment.
Callback: Once the payment is complete, M-Pesa sends a callback to the specified Callback URL.
Endpoints
PaymentServlet
Method: POST
URL: /PaymentServlet
Request Body: JSON format containing:
json
Copy code
{
  "phone": "2547xxxxxxxx",
  "amount": "100",
  "account": "datas-100"
}
Response:
Success: Returns a success message (e.g., "Payment request sent successfully!").
Error: Returns an error message (e.g., "Failed to authenticate with M-Pesa.").
Callback Endpoint
The Callback URL is specified in PaymentServlet and should be configured to handle payment status updates from M-Pesa. The format of the callback data is detailed in the M-Pesa API documentation.
Running the Application
Build the project and deploy it to Tomcat.
Start Tomcat: Ensure your server is running and accessible at http://localhost:8080/.
Open the application in a browser: Navigate to the page containing the payment form.
Test the payment process: Enter a phone number, select a data plan, and click "Buy". You should be redirected to the M-Pesa payment page to complete the transaction.
License
This project is licensed under the MIT License - see the LICENSE file for details.

