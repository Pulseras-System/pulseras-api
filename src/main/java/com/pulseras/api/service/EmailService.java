package com.pulseras.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${pulseras.openapi.fe-url}")
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
                "    <title>Cập nhật đơn hàng</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "    <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                "        <h1 style='color: white; margin: 0; font-size: 28px;'>🛍️ Pulseras</h1>" +
                "        <p style='color: #f0f0f0; margin: 10px 0 0 0; font-size: 16px;'>Thông báo cập nhật đơn hàng</p>" +
                "    </div>" +
                "    <div style='background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                "        <div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;'>" +
                "            <h2 style='color: #495057; margin: 0 0 15px 0; font-size: 20px;'>📋 Thông tin đơn hàng</h2>" +
                "            <p style='margin: 5px 0;'><strong>Mã đơn hàng:</strong> <span style='color: #007bff; font-family: monospace;'>#" + orderId + "</span></p>" +
                "        </div>" +
                "        <div style='background: #e8f4fd; padding: 20px; border-radius: 8px; border-left: 4px solid #007bff; margin-bottom: 25px;'>" +
                "            <h3 style='color: #0056b3; margin: 0 0 10px 0; font-size: 18px;'>📢 Thông báo cập nhật</h3>" +
                "            <p style='margin: 0; font-size: 16px; color: #495057;'>" + message + "</p>" +
                "        </div>" +
                "        <div style='text-align: center; margin: 30px 0;'>" +
                "            <a href='" + orderUrl + "' style='background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block; transition: all 0.3s ease;'>🔍 Xem chi tiết đơn hàng</a>" +
                "        </div>" +
                "        <div style='background: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; margin-top: 25px;'>" +
                "            <p style='margin: 0; color: #6c757d; font-size: 14px;'>Cần hỗ trợ? Liên hệ đội ngũ chăm sóc khách hàng</p>" +
                "            <p style='margin: 5px 0 0 0; color: #007bff; font-size: 14px;'>📧 support@pulseras.com | 📞 (+84) 123-456-789</p>" +
                "        </div>" +
                "    </div>" +
                "    <div style='text-align: center; margin-top: 20px; color: #6c757d; font-size: 12px;'>" +
                "        <p>© 2025 Pulseras. Tất cả quyền được bảo lưu.</p>" +
                "        <p>Email này được gửi liên quan đến đơn hàng của bạn. Vui lòng không trả lời email này.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    public void sendFeedbackEmail(String userEmail, String userName, String subject, String content) throws MessagingException {
        log.info("Sending feedback email to pulserasapp@gmail.com from user: {}", userEmail);
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo("vuquangminh12122004@gmail.com"); // Test with different email
        helper.setFrom("pulserasapp@gmail.com"); // Explicitly set sender
        helper.setReplyTo(userEmail);
        helper.setSubject("Customer Feedback: " + (subject != null && !subject.trim().isEmpty() ? subject : "General Feedback"));
        
        String htmlContent = buildFeedbackEmailTemplate(userEmail, userName, content);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
        log.info("Feedback email sent successfully to pulserasapp@gmail.com from user: {}", userEmail);
    }

    private String buildFeedbackEmailTemplate(String userEmail, String userName, String content) {
        String displayName = (userName != null && !userName.trim().isEmpty()) ? userName : "Anonymous User";
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Customer Feedback</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "    <div style='background: linear-gradient(135deg, #28a745 0%, #20c997 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                "        <h1 style='color: white; margin: 0; font-size: 28px;'>📝 Customer Feedback</h1>" +
                "        <p style='color: #f0f0f0; margin: 10px 0 0 0; font-size: 16px;'>New feedback from Pulseras customer</p>" +
                "    </div>" +
                "    <div style='background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +
                "        <div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;'>" +
                "            <h2 style='color: #495057; margin: 0 0 15px 0; font-size: 20px;'>👤 Customer Information</h2>" +
                "            <p style='margin: 5px 0;'><strong>Name:</strong> " + displayName + "</p>" +
                "            <p style='margin: 5px 0;'><strong>Email:</strong> <a href='mailto:" + userEmail + "' style='color: #007bff;'>" + userEmail + "</a></p>" +
                "            <p style='margin: 5px 0;'><strong>Date:</strong> " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>" +
                "        </div>" +
                "        <div style='background: #fff3cd; padding: 20px; border-radius: 8px; border-left: 4px solid #ffc107; margin-bottom: 25px;'>" +
                "            <h3 style='color: #856404; margin: 0 0 15px 0; font-size: 18px;'>💬 Feedback Content</h3>" +
                "            <div style='background: white; padding: 15px; border-radius: 5px; border: 1px solid #dee2e6;'>" +
                "                <p style='margin: 0; font-size: 16px; color: #495057; white-space: pre-wrap;'>" + content + "</p>" +
                "            </div>" +
                "        </div>" +
                "        <div style='background: #d1ecf1; padding: 15px; border-radius: 8px; text-align: center; margin-top: 25px;'>" +
                "            <p style='margin: 0; color: #0c5460; font-size: 14px;'>📧 You can reply directly to this email to respond to the customer</p>" +
                "        </div>" +
                "    </div>" +
                "    <div style='text-align: center; margin-top: 20px; color: #6c757d; font-size: 12px;'>" +
                "        <p>© 2025 Pulseras Admin Panel. Customer feedback system.</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}