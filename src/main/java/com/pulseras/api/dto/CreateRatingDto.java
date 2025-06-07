package com.pulseras.api.dto;

import jakarta.validation.constraints.*;

public class CreateRatingDto {

    @NotBlank
    private String accountId;

    @NotBlank
    private String productId;

    private String comment;

    @Min(1)
    @Max(5)
    private int rating;

    @NotNull
    private Integer status;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
