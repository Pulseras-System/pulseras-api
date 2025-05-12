package com.pulseras.api.controller;

import com.pulseras.api.dto.CategoryDto;
import com.pulseras.api.service.impl.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping
    public List<CategoryDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public CategoryDto create(@RequestBody CategoryDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CategoryDto update(@PathVariable String id, @RequestBody CategoryDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
