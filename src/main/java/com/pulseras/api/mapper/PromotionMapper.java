package com.pulseras.api.mapper;

import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.entity.Promotion;

public class PromotionMapper {

    public static Promotion toEntity(PromotionDto dto) {
        Promotion p = new Promotion();
        p.setId(dto.id);
        p.setPromotionId(dto.promotionId);
        p.setProductId(dto.productId);
        p.setPromotionName(dto.promotionName);
        p.setPromotionDescription(dto.promotionDescription);
        p.setDiscountPercentage(dto.discountPercentage);
        p.setStartDay(dto.startDay);
        p.setExpireDay(dto.expireDay);
        p.setStatus(dto.status);
        return p;
    }

    public static PromotionDto toDto(Promotion p) {
        PromotionDto dto = new PromotionDto();
        dto.id = p.getId();
        dto.promotionId = p.getPromotionId();
        dto.productId = p.getProductId();
        dto.promotionName = p.getPromotionName();
        dto.promotionDescription = p.getPromotionDescription();
        dto.discountPercentage = p.getDiscountPercentage();
        dto.startDay = p.getStartDay();
        dto.expireDay = p.getExpireDay();
        dto.status = p.getStatus();
        return dto;
    }
}
