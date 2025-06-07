package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreatePromotionDto;
import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.entity.Promotion;

public class PromotionMapper {

    public static Promotion toEntity(CreatePromotionDto dto) {
        Promotion p = new Promotion();
        p.setProductId(dto.getProductId());
        p.setPromotionName(dto.getPromotionName());
        p.setPromotionDescription(dto.getPromotionDescription());
        p.setDiscountPercentage(dto.getDiscountPercentage());
        p.setStartDay(dto.getStartDay());
        p.setExpireDay(dto.getExpireDay());
        p.setStatus(dto.getStatus());
        return p;
    }

    public static PromotionDto toDto(Promotion p) {
        PromotionDto dto = new PromotionDto();
        dto.setPromotionId(p.getPromotionId());
        dto.setProductId(p.getProductId());
        dto.setPromotionName(p.getPromotionName());
        dto.setPromotionDescription(p.getPromotionDescription());
        dto.setDiscountPercentage(p.getDiscountPercentage());
        dto.setStartDay(p.getStartDay());
        dto.setExpireDay(p.getExpireDay());
        dto.setStatus(p.getStatus());
        dto.setCreateDate(p.getCreateDate());
        dto.setLastEdited(p.getLastEdited());
        return dto;
    }
}
