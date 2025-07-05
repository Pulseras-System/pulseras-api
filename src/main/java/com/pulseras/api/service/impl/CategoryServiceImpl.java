package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.dto.CreateCategoryDto;
import com.pulseras.api.dto.UpdateCategoryDto;
import com.pulseras.api.entity.Category;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.CategoryMapper;
import com.pulseras.api.repository.CategoryRepository;
import com.pulseras.api.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CategoryDto> getAll() {
        return repository.findAll().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(String id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CategoryMapper.toDto(category);
    }

    @Override
    public CategoryDto create(CreateCategoryDto dto) {
        Category entity = CategoryMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());
        Category saved = repository.save(entity);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(String id, CreateCategoryDto dto) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        existing.setCategoryName(dto.getCategoryName());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());

        Category updated = repository.save(existing);
        return CategoryMapper.toDto(updated);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        repository.deleteById(id);
    }

    @Override
    public void partialUpdate(String id, UpdateCategoryDto dto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (dto.getCategoryName() != null) {
            category.setCategoryName(dto.getCategoryName());
        }
        if (dto.getStatus() != null) {
            category.setStatus(dto.getStatus());
        }
        category.setLastEdited(LocalDateTime.now());

        repository.save(category);
    }

    @Override
    public CategoryDto getByName(String name) {
        return repository.findByCategoryName(name)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
