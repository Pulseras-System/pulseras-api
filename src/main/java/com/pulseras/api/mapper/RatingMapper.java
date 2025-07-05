package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateRatingDto;
import com.pulseras.api.dto.RatingDto;
import com.pulseras.api.entity.Rating;

public class RatingMapper {

    public static Rating toEntity(CreateRatingDto dto) {
        Rating r = new Rating();
        r.setAccountId(dto.getAccountId());
        r.setProductId(dto.getProductId());
        r.setComment(dto.getComment());
        r.setRating(dto.getRating());
        r.setStatus(dto.getStatus());
        return r;
    }

    public static RatingDto toDto(Rating r) {
        return toDto(r, null);
    }

    public static RatingDto toDto(Rating r, String productName) {
        RatingDto dto = new RatingDto();
        dto.setRatingId(r.getRatingId());
        dto.setAccountId(r.getAccountId());
        dto.setProductId(r.getProductId());
        dto.setProductName(productName);
        dto.setComment(r.getComment());
        dto.setRating(r.getRating());
        dto.setStatus(r.getStatus());
        dto.setCreateDate(r.getCreateDate());
        dto.setLastEdited(r.getLastEdited());
        return dto;
    }
}