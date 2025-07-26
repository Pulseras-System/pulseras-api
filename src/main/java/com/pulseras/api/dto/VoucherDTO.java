package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {
    private String id;
    private String voucherCode;
    private String voucherName;
    private String description;
    
    // Voucher availability
    private Integer totalQuantity;
    private Integer usedQuantity;
    
    // Discount configuration
    private String discountType;
    private Double discountValue;
    private Double minOrderAmount;
    private Double maxDiscountAmount;
    
    // Usage restrictions
    private Integer maxUsagePerUser;
    private Boolean isActive;
    
    // Time restrictions
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Metadata
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
    private String banReason;
}
