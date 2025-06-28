package com.pulseras.api.controller;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sort
    ) {
        return service.getAll(keyword, categoryId, page, size, sort);
    }


    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ProductDto create(@Valid @RequestBody CreateProductDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable String id, @Valid @RequestBody CreateProductDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping("/type-distribution")
    public ResponseEntity<List<Map<String, Object>>> getProductTypeDistribution() {
        return ResponseEntity.ok(service.getProductTypeDistribution());
    }

    @GetMapping("/top-buy-products")
    public ResponseEntity<List<ProductDto>> getTopBuyProducts() {
        return ResponseEntity.ok(service.getTopBuyProducts());
    }

    @GetMapping("/latest-products")
    public ResponseEntity<List<ProductDto>> getLatestProducts() {
        return ResponseEntity.ok(service.getLatestProducts());
    }

}
