package com.pulseras.api.service;

import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.dto.ProductDto;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> getAll(String keyword, String categoryId, int page, int size, String sort);
    ProductDto getById(String id);
    ProductDto create(CreateProductDto dto);
    ProductDto update(String id, CreateProductDto dto);
    void delete(String id);
    List<Map<String, Object>> getProductTypeDistribution();
    List<ProductDto> getTopBuyProducts();
    List<ProductDto> getLatestProducts();
}
