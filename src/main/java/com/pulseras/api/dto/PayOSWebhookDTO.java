package com.pulseras.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOSWebhookDTO {
    private String code;
    private String desc;
    private Boolean success;
    private PayOSWebhookData data;
    private String signature;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayOSWebhookData {
        private Long orderCode;
        private Integer amount;
        private String description;
        private String accountNumber;
        private String reference;
        private String transactionDateTime;
        private String currency;
        private String paymentLinkId;
        private String code;
        private String desc;
        private String counterAccountBankId;
        private String counterAccountBankName;
        private String counterAccountName;
        private String counterAccountNumber;
        private String virtualAccountName;
        private String virtualAccountNumber;
    }
}
