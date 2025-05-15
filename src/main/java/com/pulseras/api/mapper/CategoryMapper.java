package com.pulseras.api.mapper;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CategoryDto dto) {
        Category c = new Category();
        c.setId(dto.id);
        c.setCategoryId(dto.categoryId);
        c.setProductId(dto.productId);
        c.setCategoryName(dto.categoryName);
        c.setStatus(dto.status);
        return c;
    }

    public static CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.id = c.getId();
        dto.categoryId = c.getCategoryId();
        dto.productId = c.getProductId();
        dto.categoryName = c.getCategoryName();
        dto.status = c.getStatus();
        return dto;
    }
}
