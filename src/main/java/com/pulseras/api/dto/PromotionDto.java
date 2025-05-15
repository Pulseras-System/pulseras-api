package com.pulseras.api.dto;

import java.time.LocalDateTime;

public class PromotionDto {
    public String id;
    public int promotionId;
    public int productId;
    public String promotionName;
    public String promotionDescription;
    public double discountPercentage;
    public LocalDateTime startDay;
    public LocalDateTime expireDay;
    public int status;
}
