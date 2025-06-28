package com.pulseras.api.dto;

import lombok.Data;

@Data
public class UpdateOrderDTO {
    private String orderInfor;
    private Integer amount;
    private String voucherId;
    private Double totalPrice;
    private Integer status;
}
