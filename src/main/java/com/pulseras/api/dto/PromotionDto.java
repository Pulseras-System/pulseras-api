package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDto {

    private String promotionId;
    private String productId;
    private String productName;
    private String promotionName;
    private String promotionDescription;
    private double discountPercentage;
    private LocalDateTime startDay;
    private LocalDateTime expireDay;
    private int status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
