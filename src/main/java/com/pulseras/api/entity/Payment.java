package com.pulseras.api.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    // Từ cả CheckoutResponseData và WebhookData
    private long orderCode;
    private int amount;
    private String description;
    private String accountNumber;
    private String accountName;
    private String currency;
    private String paymentLinkId;
    private String status;
    private String checkoutUrl;
    private String qrCode;
    private String bin;

    // Dữ liệu khi xác nhận thanh toán
    private String reference;
    private String transactionDateTime;

    // Dữ liệu từ webhook (nếu có)
    private String virtualAccountName;
    private String virtualAccountNumber;

    private String counterAccountBankId;
    private String counterAccountBankName;
    private String counterAccountName;
    private String counterAccountNumber;

    // Dữ liệu trạng thái link thanh toán (nếu bị huỷ)
    private String createdAt;
    private String canceledAt;
    private String cancellationReason;

    // Danh sách giao dịch (từ getPaymentLinkInformation hoặc cancelPaymentLink)
    private List<Transaction> transactions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        private String reference;
        private int amount;
        private String accountNumber;
        private String description;
        private String transactionDateTime;

        private String virtualAccountName;
        private String virtualAccountNumber;

        private String counterAccountBankId;
        private String counterAccountBankName;
        private String counterAccountName;
        private String counterAccountNumber;
    }
}
