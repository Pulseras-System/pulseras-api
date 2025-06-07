package com.pulseras.api.dto;

import jakarta.validation.constraints.*;

public class CreateFeedbackDto {

    @NotBlank
    private String accountId;

    @NotBlank
    private String productId;

    @NotBlank
    private String feedbackInfor;

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

    public String getFeedbackInfor() {
        return feedbackInfor;
    }

    public void setFeedbackInfor(String feedbackInfor) {
        this.feedbackInfor = feedbackInfor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
