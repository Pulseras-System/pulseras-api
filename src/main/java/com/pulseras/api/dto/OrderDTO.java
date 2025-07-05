package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private String id;
    private String orderInfor;
    private Integer amount;
    private String accountId;
    private String voucherId;
    private Double totalPrice;
    private Integer status;
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
    private List<OrderDetailDTO> orderDetails;
}
