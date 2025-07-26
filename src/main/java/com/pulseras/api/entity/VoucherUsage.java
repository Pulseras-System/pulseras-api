package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "voucher_usages")
@CompoundIndex(def = "{'accountId': 1, 'voucherId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsage {
    @Id
    private ObjectId id;
    
    private String accountId;
    private String voucherId;
    private String orderId;
    private LocalDateTime usedAt;
    private Double discountAmount;
    private Double originalAmount;
    private Double finalAmount;
}
