package com.pulseras.api.service.impl;

import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.entity.Payment;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.repository.PaymentRepository;
import com.pulseras.api.service.OrderDetailService;
import com.pulseras.api.service.OrderService;
import com.pulseras.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PayOS payOS;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    @Value("${payos.webhook-url}")
    private String webhookUrl;

    @Override
    public Payment createPaymentLink(String orderId) throws Exception {
        OrderDTO order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        List<OrderDetailDTO> orderDetails = orderDetailService.getAllOrderDetailsByOrderId(orderId);

        List<ItemData> items = orderDetails.stream().map(detail ->
                ItemData.builder()
                        .name("Product ID: " + detail.getProductId())
                        .quantity(detail.getQuantity())
                        .price(detail.getPrice().intValue())
                        .build()
        ).collect(Collectors.toList());

        long orderCode = System.currentTimeMillis();
        int totalAmount = order.getTotalPrice().intValue();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(totalAmount)
                .description("Thanh toán đơn hàng #" + orderId)
                .returnUrl(webhookUrl + "/success")
                .cancelUrl(webhookUrl + "/cancel")
                .items(items)
                .build();

        CheckoutResponseData checkoutData = payOS.createPaymentLink(paymentData);

        Payment payment = Payment.builder()
                .orderCode(orderCode)
                .amount(checkoutData.getAmount())
                .description(checkoutData.getDescription())
                .orderCode(Instant.now().toEpochMilli())
                .status(checkoutData.getStatus())
                .paymentLinkId(checkoutData.getPaymentLinkId())
                .checkoutUrl(checkoutData.getCheckoutUrl())
                .qrCode(checkoutData.getQrCode())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public Payment handleWebhook(Webhook webhookBody) throws Exception {
        WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

        Payment payment = paymentRepository.findByOrderCode(data.getOrderCode())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for orderCode: " + data.getOrderCode()));

        payment.setAmount(data.getAmount());
        payment.setDescription(data.getDescription());
        payment.setAccountNumber(data.getAccountNumber());
        payment.setReference(data.getReference());
        payment.setTransactionDateTime(data.getTransactionDateTime());
        payment.setCurrency(data.getCurrency());
        payment.setPaymentLinkId(data.getPaymentLinkId());
        payment.setStatus(data.getCode());

        payment.setCounterAccountBankId(data.getCounterAccountBankId());
        payment.setCounterAccountBankName(data.getCounterAccountBankName());
        payment.setCounterAccountName(data.getCounterAccountName());
        payment.setCounterAccountNumber(data.getCounterAccountNumber());
        payment.setVirtualAccountName(data.getVirtualAccountName());
        payment.setVirtualAccountNumber(data.getVirtualAccountNumber());

        return paymentRepository.save(payment);
    }
}
