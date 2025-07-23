package com.pulseras.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderDTO {
    private String orderInfor;
    private Integer amount;
    private String voucherId;
    private Double totalPrice;
    private String paymentMethod;
    private Integer status;
    private LocalDateTime lastEdited;
}
