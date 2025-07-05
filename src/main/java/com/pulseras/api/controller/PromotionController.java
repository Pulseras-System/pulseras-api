package com.pulseras.api.controller;

import com.pulseras.api.dto.CreatePromotionDto;
import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService service;

    public PromotionController(PromotionService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "promotionName") String sort
    ) {
        return service.getAll(keyword, page, size, sort);
    }

    @GetMapping("/{id}")
    public PromotionDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public PromotionDto create(@Valid @RequestBody CreatePromotionDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public PromotionDto update(@PathVariable String id, @Valid @RequestBody CreatePromotionDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
