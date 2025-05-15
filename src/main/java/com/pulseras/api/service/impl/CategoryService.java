package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.entity.Category;
import com.pulseras.api.mapper.CategoryMapper;
import com.pulseras.api.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    public List<CategoryDto> findAll() {
        return categoryRepo.findAll().stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    public CategoryDto findById(String id) {
        return categoryRepo.findById(id).map(CategoryMapper::toDto).orElse(null);
    }

    public CategoryDto create(CategoryDto dto) {
        Category saved = categoryRepo.save(CategoryMapper.toEntity(dto));
        return CategoryMapper.toDto(saved);
    }

    public CategoryDto update(String id, CategoryDto dto) {
        Category existing = categoryRepo.findById(id).orElseThrow();
        Category updated = CategoryMapper.toEntity(dto);
        updated.setId(id);
        return CategoryMapper.toDto(categoryRepo.save(updated));
    }

    public void delete(String id) {
        categoryRepo.deleteById(id);
    }
}
