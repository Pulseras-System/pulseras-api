package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateWishlistDto;
import com.pulseras.api.dto.WishlistDto;
import com.pulseras.api.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService service;

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
    public WishlistDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public WishlistDto create(@Valid @RequestBody CreateWishlistDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public WishlistDto update(@PathVariable String id, @Valid @RequestBody CreateWishlistDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
