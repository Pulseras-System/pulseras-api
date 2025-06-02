package com.pulseras.api.mapper;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.dto.CreateCategoryDto;
import com.pulseras.api.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CreateCategoryDto dto) {
        Category c = new Category();
        c.setCategoryName(dto.getCategoryName());
        c.setStatus(dto.getStatus());
        return c;
    }

    public static CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(c.getCategoryId());
        dto.setCategoryName(c.getCategoryName());
        dto.setStatus(c.getStatus());
        dto.setCreateDate(c.getCreateDate());
        dto.setLastEdited(c.getLastEdited());
        return dto;
    }
}
