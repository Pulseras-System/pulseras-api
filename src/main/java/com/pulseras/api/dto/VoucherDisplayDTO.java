package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDisplayDTO {
    private String id;
    private String voucherCode;
    private String voucherName;
    private String description;
    private String discountType; // "PERCENTAGE" or "FIXED_AMOUNT"
    private Double discountValue;
    private Double minOrderAmount;
    private Double maxDiscountAmount;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean canUse; // Whether current user can use this voucher
    private Boolean isExpired;
    private Boolean isOutOfStock;
    private String usageStatus; // "AVAILABLE", "USED", "EXPIRED", "OUT_OF_STOCK"
}
