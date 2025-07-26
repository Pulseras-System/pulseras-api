package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherApplicationResultDTO {
    private Boolean success;
    private String message;
    private String voucherCode;
    private Double originalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String discountType;
    private Double discountValue;
}
