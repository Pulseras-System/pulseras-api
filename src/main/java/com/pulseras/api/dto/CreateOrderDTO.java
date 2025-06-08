package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {
    private String orderInfor;
    private Integer amount;
    private Integer accountId;
    private Integer voucherId;
    private Double totalPrice;
    private Integer status;
    private LocalDateTime lastEdited;
}
