package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    private ObjectId id;

    @Indexed
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
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
}
