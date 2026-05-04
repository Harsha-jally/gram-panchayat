package com.harsha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendStatusUpdateEmail(String toEmail, String name, String status) {

        if (toEmail == null || toEmail.isEmpty()) {
            System.out.println("No email found, skipping...");
            return;
        }

        String messageText;

        switch (status) {
            case "RESOLVED":
                messageText = "Good news! Your complaint has been RESOLVED.";
                break;
            case "REJECTED":
                messageText = "Your complaint has been REJECTED.";
                break;
            case "IN_PROGRESS":
                messageText = "Your complaint is now IN PROGRESS.";
                break;
            default:
                messageText = "Your complaint status is: " + status;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("jallyharsha@gmail.com");
        message.setTo(toEmail);
        message.setSubject("RuralFix - Complaint Status Update");

        message.setText(
                "Hello " + name + ",\n\n" +
                        messageText + "\n\n" +
                        "Regards,\nRuralFix Team"
        );

        mailSender.send(message);

        System.out.println("Email sent successfully!");
    }
}