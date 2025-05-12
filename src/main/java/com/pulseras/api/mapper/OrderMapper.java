package com.pulseras.api.mapper;

import com.pulseras.api.dto.OrderDto;
import com.pulseras.api.entity.Order;

public class OrderMapper {

    public static Order toEntity(OrderDto dto) {
        Order o = new Order();
        o.setId(dto.id);
        o.setOrderId(dto.orderId);
        o.setOrderInfor(dto.orderInfor);
        o.setAmount(dto.amount);
        o.setAccountId(dto.accountId);
        o.setVoucherId(dto.voucherId);
        o.setTotalPrice(dto.totalPrice);
        o.setStatus(dto.status);
        o.setCreateDate(dto.createDate != null ? dto.createDate : o.getCreateDate());
        o.setLastEdited(dto.lastEdited != null ? dto.lastEdited : o.getLastEdited());
        return o;
    }

    public static OrderDto toDto(Order o) {
        OrderDto dto = new OrderDto();
        dto.id = o.getId();
        dto.orderId = o.getOrderId();
        dto.orderInfor = o.getOrderInfor();
        dto.amount = o.getAmount();
        dto.accountId = o.getAccountId();
        dto.voucherId = o.getVoucherId();
        dto.totalPrice = o.getTotalPrice();
        dto.status = o.getStatus();
        dto.createDate = o.getCreateDate();
        dto.lastEdited = o.getLastEdited();
        return dto;
    }
}
