package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private String id;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Double price;
    private Integer promotionId;
    private Integer status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
