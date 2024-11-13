package com.eliud.mpesa;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsSender {
    // Twilio Account SID, Auth Token, and your Twilio phone number
    public static final String ACCOUNT_SID = "ACb4219da789b8d3f368b806def0b49287"; 
    public static final String AUTH_TOKEN = "5dcedd1ec8937d784fbde28cd3ceec48";
    public static final String TWILIO_PHONE_NUMBER = "+254702839859";

    // Initialize Twilio with your credentials
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Method to send SMS
    public static void sendSms(String toPhoneNumber, String messageContent) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),    // The recipient's phone number
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Your Twilio phone number
                    messageContent)
                .create();
            System.out.println("SMS sent successfully with SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
