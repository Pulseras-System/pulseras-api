package com.pulseras.api.controller;

import com.pulseras.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-order-update")
    public ResponseEntity<String> sendOrderUpdateEmail(
            @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request.getTo(), request.getMessage(), request.getOrderId());
            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/send-password-reset")
    public ResponseEntity<String> sendPasswordResetEmail(
            @RequestBody PasswordResetRequest request) {
        try {
            emailService.sendPasswordResetEmail(request.getTo(), request.getToken());
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // DTOs for request bodies
    public static class EmailRequest {
        private String to;
        private String message;
        private String orderId;

        // Constructors
        public EmailRequest() {}

        public EmailRequest(String to, String message, String orderId) {
            this.to = to;
            this.message = message;
            this.orderId = orderId;
        }

        // Getters and Setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }

    public static class PasswordResetRequest {
        private String to;
        private String token;

        // Constructors
        public PasswordResetRequest() {}

        public PasswordResetRequest(String to, String token) {
            this.to = to;
            this.token = token;
        }

        // Getters and Setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
