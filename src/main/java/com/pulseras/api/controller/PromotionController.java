package com.pulseras.api.controller;

import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.service.impl.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService service;

    @GetMapping
    public List<PromotionDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PromotionDto getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public PromotionDto create(@RequestBody PromotionDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public PromotionDto update(@PathVariable String id, @RequestBody PromotionDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
