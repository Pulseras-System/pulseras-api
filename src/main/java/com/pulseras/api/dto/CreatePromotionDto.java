package com.pulseras.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class CreatePromotionDto {

    @NotBlank
    private String productId;

    @NotBlank
    private String promotionName;

    private String promotionDescription;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double discountPercentage;

    @NotNull
    private LocalDateTime startDay;

    @NotNull
    private LocalDateTime expireDay;

    @NotNull
    private Integer status;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getStartDay() {
        return startDay;
    }

    public void setStartDay(LocalDateTime startDay) {
        this.startDay = startDay;
    }

    public LocalDateTime getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(LocalDateTime expireDay) {
        this.expireDay = expireDay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
