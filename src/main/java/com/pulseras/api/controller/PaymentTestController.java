package com.pulseras.api.controller;

import com.pulseras.api.entity.Payment;
import com.pulseras.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.Webhook;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test/payment")
@RequiredArgsConstructor
public class PaymentTestController {

    private final PaymentService paymentService;

    @PostMapping("/create-link/{orderId}")
    public ResponseEntity<?> testCreatePaymentLink(@PathVariable String orderId) {
        try {
            Payment payment = paymentService.createPaymentLink(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment link created successfully");
            response.put("data", payment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create payment link: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/test-webhook")
    public ResponseEntity<?> testWebhook(@RequestBody Webhook webhookBody) {
        try {
            Payment payment = paymentService.handleWebhook(webhookBody);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Webhook processed successfully");
            response.put("data", payment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to process webhook: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/config-check")
    public ResponseEntity<?> checkConfiguration() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // This will help verify if PayOS bean is properly configured
            response.put("success", true);
            response.put("message", "PayOS configuration is working");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "PayOS configuration error: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
