package com.pulseras.api.controller;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto create(
            @RequestParam("categoryIds") List<String> categoryIds,
            @RequestParam("productName") String productName,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("productMaterial") String productMaterial,
            @RequestParam("quantity") int quantity,
            @RequestParam("type") String type,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") Integer status,
            @RequestPart("image") MultipartFile image
    ) {
        CreateProductDto dto = new CreateProductDto();
        dto.setCategoryIds(categoryIds);
        dto.setProductName(productName);
        dto.setProductDescription(productDescription);
        dto.setProductMaterial(productMaterial);
        dto.setQuantity(quantity);
        dto.setType(type);
        dto.setPrice(price);
        dto.setStatus(status);

        return service.create(dto, image);
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
