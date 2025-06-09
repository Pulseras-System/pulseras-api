package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateOrderDetailDTO;
import com.pulseras.api.dto.OrderDetailDTO;
import com.pulseras.api.entity.OrderDetail;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class OrderDetailMapper {

    public static OrderDetailDTO toDTO(OrderDetail entity) {
        return OrderDetailDTO.builder()
                .id(entity.getId().toHexString())
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .promotionId(entity.getPromotionId())
                .status(entity.getStatus())
                .createDate(entity.getCreateDate())
                .lastEdited(entity.getLastEdited())
                .build();
    }

    public static OrderDetail toEntity(CreateOrderDetailDTO dto) {
        return OrderDetail.builder()
                .id(new ObjectId())
                .orderId(dto.getOrderId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .promotionId(dto.getPromotionId())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createDate(LocalDateTime.now())
                .build();
    }
}
