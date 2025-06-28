package com.pulseras.api.controller;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.dto.CreateCategoryDto;
import com.pulseras.api.dto.UpdateCategoryDto;
import com.pulseras.api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateCategoryDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @Valid @RequestBody CreateCategoryDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public void partialUpdate(@PathVariable String id, @RequestBody UpdateCategoryDto dto) {
        service.partialUpdate(id, dto);
    }
}
