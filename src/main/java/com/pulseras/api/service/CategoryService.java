package com.pulseras.api.service;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.dto.CreateCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll();
    CategoryDto getById(String id);
    void create(CreateCategoryDto dto);
    void update(String id, CreateCategoryDto dto);
    void delete(String id);
}
