package com.pulseras.api.dto;

import java.time.LocalDateTime;

public class OrderDto {
    public String id;
    public int orderId;
    public String orderInfor;
    public int amount;
    public int accountId;
    public int voucherId;
    public double totalPrice;
    public int status;
    public LocalDateTime createDate;
    public LocalDateTime lastEdited;
}
