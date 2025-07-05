package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVoucherDTO {
    private String voucherName;
    private String accountId; // Account that owns this voucher
    private Integer voucherQuantity;
    private Double minPrice;
    private Double maxDiscount;
    private Double discountPercentage;
    private LocalDateTime startDay;
    private LocalDateTime expireDay;
    private Integer status;
    private LocalDateTime lastEdited;
}
