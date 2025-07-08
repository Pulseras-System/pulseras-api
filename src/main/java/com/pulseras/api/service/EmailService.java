package com.pulseras.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${pulseras.fe.dev-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        String htmlContent = "<h1>Reset Your Password</h1>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>" +
                "<p>This link will expire in 30 minutes.</p>";
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }


    public void sendEmail(String to, String message, String orderId) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject("Order Update - Order #" + orderId);
        
        String orderUrl = frontendUrl + "/orders/" + orderId;
        String htmlContent = buildEmailTemplate(message, orderId, orderUrl);
        
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    private String buildEmailTemplate(String message, String orderId, String orderUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Order Update</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "    <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                "        <h1 style='color: white; margin: 0; font-size: 28px;'>🛍️ Pulseras</h1>" +
                "        <p style='color: #f0f0f0; margin: 10px 0 0 0; font-size: 16px;'>Order Update Notification</p>" +
                "    </div>" +
                "    <div style='background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                "        <div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;'>" +
                "            <h2 style='color: #495057; margin: 0 0 15px 0; font-size: 20px;'>📋 Order Information</h2>" +
                "            <p style='margin: 5px 0;'><strong>Order ID:</strong> <span style='color: #007bff; font-family: monospace;'>#" + orderId + "</span></p>" +
                "        </div>" +
                "        <div style='background: #e8f4fd; padding: 20px; border-radius: 8px; border-left: 4px solid #007bff; margin-bottom: 25px;'>" +
                "            <h3 style='color: #0056b3; margin: 0 0 10px 0; font-size: 18px;'>📢 Update Message</h3>" +
                "            <p style='margin: 0; font-size: 16px; color: #495057;'>" + message + "</p>" +
                "        </div>" +
                "        <div style='text-align: center; margin: 30px 0;'>" +
                "            <a href='" + orderUrl + "' style='background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block; transition: all 0.3s ease;'>🔍 View Order Details</a>" +
                "        </div>" +
                "        <div style='background: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; margin-top: 25px;'>" +
                "            <p style='margin: 0; color: #6c757d; font-size: 14px;'>Need help? Contact our support team</p>" +
                "            <p style='margin: 5px 0 0 0; color: #007bff; font-size: 14px;'>📧 support@pulseras.com | 📞 (+84) 123-456-789</p>" +
                "        </div>" +
                "    </div>" +
                "    <div style='text-align: center; margin-top: 20px; color: #6c757d; font-size: 12px;'>" +
                "        <p>© 2025 Pulseras. All rights reserved.</p>" +
                "        <p>This email was sent regarding your order. Please do not reply to this email.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}