package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateOrderDTO;
import com.pulseras.api.dto.OrderDTO;
import com.pulseras.api.entity.Order;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class OrderMapper {

    public static OrderDTO toDTO(Order entity) {
        return OrderDTO.builder()
                .id(entity.getId().toHexString())
                .orderInfor(entity.getOrderInfor())
                .amount(entity.getAmount())
                .accountId(entity.getAccountId())
                .voucherId(entity.getVoucherId())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .lastEdited(entity.getLastEdited())
                .createDate(entity.getCreateDate())
                .build();
    }

    public static Order toEntity(CreateOrderDTO dto) {
        return Order.builder()
                .id(new ObjectId())
                .orderInfor(dto.getOrderInfor())
                .amount(dto.getAmount())
                .accountId(dto.getAccountId())
                .voucherId(dto.getVoucherId())
                .totalPrice(dto.getTotalPrice())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createDate(LocalDateTime.now())
                .build();
    }
}
