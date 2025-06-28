package com.pulseras.api.controller;

import com.pulseras.api.entity.Payment;
import com.pulseras.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<Payment> createPayment(@PathVariable String orderId) throws Exception {
        return ResponseEntity.ok(paymentService.createPaymentLink(orderId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Payment> handleWebhook(@RequestBody Webhook webhook) throws Exception {
        return ResponseEntity.ok(paymentService.handleWebhook(webhook));
    }

}
