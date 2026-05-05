package com.rsandoval.ecommerce_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - E-Commerce Store");
        message.setText("Hello,\n\n" +
                "You have requested to reset your password. Please click on the link below to set a new password:\n\n" +
                resetUrl + "\n\n" +
                "This link will expire in 15 minutes. If you did not request this reset, please ignore this email.\n\n" +
                "Thank you.");

        mailSender.send(message);
        System.out.println("Password reset email sent successfully to: " + toEmail);
    }

    @Async
    public void sendOrderConfirmationEmail(String toEmail, Long orderId) {
        try {
            System.out.println("Starting email task on thread: " + Thread.currentThread().getName());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - #" + orderId);
            message.setText("Thank you for your purchase!\n\n" +
                    "Your order (#" + orderId + ") has been successfully processed and will be shipped soon.\n\n" +
                    "Thank you for shopping with us!");

            mailSender.send(message);
            System.out.println("Email successfully sent for Order #" + orderId);
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }
}
