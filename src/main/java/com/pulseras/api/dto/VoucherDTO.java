package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {
    private String id;
    private String voucherName;
    private String accountId; // Account that owns this voucher
    private List<String> usedByAccounts; // List of account IDs that have used this voucher
    private Integer voucherQuantity;
    private Double minPrice;
    private Double maxDiscount;
    private Double discountPercentage;
    private LocalDateTime startDay;
    private LocalDateTime expireDay;
    private Integer status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
