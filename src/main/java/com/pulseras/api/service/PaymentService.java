package com.pulseras.api.service;

import com.pulseras.api.entity.Payment;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

public interface PaymentService {
    Payment createPaymentLink(String orderId) throws Exception;
    Payment handleWebhook(Webhook webhookBody) throws Exception;
}
