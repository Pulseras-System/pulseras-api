package com.pulseras.api.dto;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderDetailDTO {
    private String productId;
    private Integer quantity;
    private Double price;
    private Integer promotionId;
    private Integer status;
    private LocalDateTime lastEdited;
}
