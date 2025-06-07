package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateRatingDto;
import com.pulseras.api.dto.RatingDto;
import com.pulseras.api.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sort
    ) {
        return service.getAll(keyword, page, size, sort);
    }

    @GetMapping("/{id}")
    public RatingDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateRatingDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @Valid @RequestBody CreateRatingDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
