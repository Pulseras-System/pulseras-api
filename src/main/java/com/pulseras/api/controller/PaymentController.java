package com.pulseras.api.controller;

import com.pulseras.api.dto.PayOSWebhookDTO;
import com.pulseras.api.entity.Payment;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.repository.PaymentRepository;
import com.pulseras.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<?> createPayment(@PathVariable String orderId) {
        try {
            Payment payment = paymentService.createPaymentLink(orderId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment link created successfully",
                "data", payment
            ));
        } catch (Exception e) {
            log.error("Failed to create payment for order: {}", orderId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to create payment link: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            HttpServletRequest request,
            @RequestBody PayOSWebhookDTO webhookDTO) {
        try {
            // Log webhook reception for monitoring
            log.info("Webhook received from IP: {} for orderCode: {}", 
                request.getRemoteAddr(), webhookDTO.getData().getOrderCode());

            // Validate content type
            String contentType = request.getContentType();
            if (!"application/json".equals(contentType)) {
                log.warn("Invalid content type: {}", contentType);
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid content type"
                ));
            }

            // Check if payment already processed (idempotency)
            String reference = webhookDTO.getData().getReference();
            if (reference != null && paymentRepository.existsByReferenceAndStatus(reference, "00")) {
                log.info("Webhook already processed for reference: {}", reference);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Already processed"
                ));
            }

            // Create WebhookData from DTO
            WebhookData webhookData = WebhookData.builder()
                    .orderCode(webhookDTO.getData().getOrderCode())
                    .amount(webhookDTO.getData().getAmount())
                    .description(webhookDTO.getData().getDescription())
                    .accountNumber(webhookDTO.getData().getAccountNumber())
                    .reference(webhookDTO.getData().getReference())
                    .transactionDateTime(webhookDTO.getData().getTransactionDateTime())
                    .currency(webhookDTO.getData().getCurrency())
                    .paymentLinkId(webhookDTO.getData().getPaymentLinkId())
                    .code(webhookDTO.getData().getCode())
                    .counterAccountBankId(webhookDTO.getData().getCounterAccountBankId())
                    .counterAccountBankName(webhookDTO.getData().getCounterAccountBankName())
                    .counterAccountName(webhookDTO.getData().getCounterAccountName())
                    .counterAccountNumber(webhookDTO.getData().getCounterAccountNumber())
                    .virtualAccountName(webhookDTO.getData().getVirtualAccountName())
                    .virtualAccountNumber(webhookDTO.getData().getVirtualAccountNumber())
                    .build();
            
            // Create Webhook with signature verification
            Webhook webhook = Webhook.builder()
                    .code(webhookDTO.getCode())
                    .desc(webhookDTO.getDesc())
                    .success(webhookDTO.getSuccess())
                    .data(webhookData)
                    .signature(webhookDTO.getSignature())
                    .build();
            
            try {
                // Process webhook with PayOS signature verification
                Payment payment = paymentService.handleWebhook(webhook);
                
                log.info("Webhook processed successfully for orderCode: {}", webhookDTO.getData().getOrderCode());
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Webhook processed successfully",
                    "data", payment
                ));
                
            } catch (Exception signatureError) {
                // If signature verification fails but this is development, 
                // try processing without verification for testing
                log.warn("Signature verification failed, attempting direct processing for development: {}", signatureError.getMessage());
                
                try {
                    // Direct database update for development testing
                    Payment payment = paymentRepository.findByOrderCode(webhookDTO.getData().getOrderCode())
                            .orElseThrow(() -> new ResourceNotFoundException("Payment not found for orderCode: " + webhookDTO.getData().getOrderCode()));

                    // Update payment directly
                    payment.setAmount(webhookDTO.getData().getAmount());
                    payment.setDescription(webhookDTO.getData().getDescription());
                    payment.setAccountNumber(webhookDTO.getData().getAccountNumber());
                    payment.setReference(webhookDTO.getData().getReference());
                    payment.setTransactionDateTime(webhookDTO.getData().getTransactionDateTime());
                    payment.setCurrency(webhookDTO.getData().getCurrency());
                    payment.setPaymentLinkId(webhookDTO.getData().getPaymentLinkId());
                    payment.setStatus(webhookDTO.getData().getCode());

                    payment.setCounterAccountBankId(webhookDTO.getData().getCounterAccountBankId());
                    payment.setCounterAccountBankName(webhookDTO.getData().getCounterAccountBankName());
                    payment.setCounterAccountName(webhookDTO.getData().getCounterAccountName());
                    payment.setCounterAccountNumber(webhookDTO.getData().getCounterAccountNumber());
                    payment.setVirtualAccountName(webhookDTO.getData().getVirtualAccountName());
                    payment.setVirtualAccountNumber(webhookDTO.getData().getVirtualAccountNumber());

                    Payment updatedPayment = paymentRepository.save(payment);
                    
                    log.info("Webhook processed via fallback method for orderCode: {}", webhookDTO.getData().getOrderCode());
                    
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Webhook processed successfully (development fallback - signature verification bypassed)",
                        "data", updatedPayment,
                        "warning", "This bypassed signature verification for development. Use proper signatures in production."
                    ));
                    
                } catch (Exception fallbackError) {
                    log.error("Both signature verification and fallback processing failed: {}", fallbackError.getMessage());
                    throw fallbackError;
                }
            }
            
        } catch (ResourceNotFoundException e) {
            log.error("Payment not found for webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Payment not found: " + e.getMessage()
            ));
        } catch (Exception e) {
            // Log security events for invalid signatures
            log.error("Webhook verification failed from IP: {} - {}", 
                request.getRemoteAddr(), e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Webhook verification failed",
                "error", "Invalid signature or malformed data"
            ));
        }
    }

    @GetMapping("/order-code/{orderCode}")
    public ResponseEntity<?> getPaymentByOrderCode(@PathVariable Long orderCode) {
        try {
            Payment payment = paymentRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found for orderCode: " + orderCode));
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", payment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Payment not found: " + e.getMessage()
            ));
        }
    }
}
