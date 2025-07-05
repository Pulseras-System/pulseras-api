package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {
    private String orderInfor;
    private Integer amount;
    private String accountId;
    private String voucherId;
    private Double totalPrice;
    private Integer status;
    private LocalDateTime lastEdited;
    
    // Optional fields for cart operations
    private String productId;        // For adding a single product to cart
    private List<String> productIds; // For adding multiple products to cart
}
