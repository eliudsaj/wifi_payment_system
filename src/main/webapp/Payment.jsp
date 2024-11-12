<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manual Payment</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body {
            background-color: #f5f5f5;
            font-family: Arial, sans-serif;
        }
        .payment-container {
            max-width: 500px;
            margin: 2rem auto;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            padding: 2rem;
        }
        .instructions {
            color: #333;
        }
        .payment-form label {
            font-weight: bold;
        }
    </style>
</head>
<body>

<div class="payment-container">
    <h3 class="text-center">Manual Payment</h3>
    <p class="text-center text-muted">Follow the steps below to complete your payment manually.</p>

    <div class="instructions my-3">
        <p><strong>Instructions:</strong></p>
        <ol>
            <li>Open the M-PESA menu on your phone.</li>
            <li>Select <strong>Lipa na M-PESA</strong> and choose <strong>PayBill</strong>.</li>
            <li>Enter the following details:
                <ul>
                    <li><strong>PayBill Number:</strong> 123456</li> <!-- Replace with your actual PayBill number -->
                    <li><strong>Account Number:</strong> Enter your phone number in the format 2547xxxxxxxx</li>
                </ul>
            </li>
            <li>Enter the amount you want to pay.</li>
            <li>Complete the transaction and note down the <strong>Transaction ID</strong>.</li>
            <li>Fill in the form below with your phone number, transaction ID, and the amount paid.</li>
        </ol>
    </div>

    <form id="manualPaymentForm" class="payment-form">
        <div class="form-group">
            <label for="phone">Phone Number</label>
            <input type="text" class="form-control" id="phone" placeholder="Enter your phone number in format 2547xxxxxxxx" required>
        </div>
        <div class="form-group">
            <label for="transactionId">Transaction ID</label>
            <input type="text" class="form-control" id="transactionId" placeholder="Enter the transaction ID" required>
        </div>
        <div class="form-group">
            <label for="amount">Amount</label>
            <input type="number" class="form-control" id="amount" placeholder="Enter the amount paid" required min="1" readonly>
        </div>
        <button type="submit" class="btn btn-primary btn-block">Submit Payment Details</button>
    </form>
</div>

<script>
    $(document).ready(function () {
        // Capture amount passed from the URL (query parameter)
        const urlParams = new URLSearchParams(window.location.search);
        const amount = urlParams.get('amount');

        // If amount is found in the URL, set it in the amount input field
        if (amount) {
            $("#amount").val(amount); // Pre-fill the amount field
        }

        // Handle form submission
        $("#manualPaymentForm").on("submit", function (e) {
            e.preventDefault();

            const phone = $("#phone").val();
            const transactionId = $("#transactionId").val();
            const amount = parseFloat($("#amount").val());

            // Simple validation
            if (phone.length !== 12 || !phone.startsWith("2547")) {
                Swal.fire("Error", "Please enter a valid phone number in the format 2547xxxxxxxx", "error");
                return;
            }
            if (!transactionId) {
                Swal.fire("Error", "Transaction ID is required.", "error");
                return;
            }
            if (isNaN(amount) || amount <= 0) {
                Swal.fire("Error", "Please enter a valid amount.", "error");
                return;
            }

            const paymentData = {
                phone: phone,
                transactionId: transactionId,
                amount: amount
            };

            // Send payment data to server for manual verification
            $.ajax({
                url: 'ManualPaymentServlet', // Ensure this URL matches your backend servlet URL
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(paymentData),
                success: function (response) {
                    if (response.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Success',
                            text: 'Payment details submitted successfully! Your payment will be verified shortly.',
                            confirmButtonText: 'OK'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.href = 'login.html'; // Redirect to login page after success
                            }
                        });
                    } else {
                        Swal.fire("Error", response.message, "error");
                    }
                },
                error: function () {
                    Swal.fire("Error", "An error occurred. Please try again.", "error");
                }
            });
        });
    });
</script>

</body>
</html>
