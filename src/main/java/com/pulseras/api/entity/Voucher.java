package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Document(collection = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    private ObjectId id;

    @Indexed
    private String voucherCode; // Unique voucher code (e.g., "SAVE20", "WELCOME10")
    
    private String voucherName; // Display name for the voucher
    private String description; // Description of the voucher
    
    // Voucher availability
    private Integer totalQuantity; // Total available vouchers
    private Integer usedQuantity; // How many have been used
    
    // Discount configuration
    private String discountType; // "PERCENTAGE" or "FIXED_AMOUNT"
    private Double discountValue; // 20 (for 20%) or 50000 (for 50,000 VND)
    private Double minOrderAmount; // Minimum order amount to use voucher
    private Double maxDiscountAmount; // Maximum discount cap
    
    // Usage restrictions
    private Integer maxUsagePerUser; // How many times one user can use (usually 1)
    private Boolean isActive; // Whether voucher is currently active
    
    // Time restrictions
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Metadata
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
    
    @Nullable
    private String banReason; // If voucher is banned/disabled
}
