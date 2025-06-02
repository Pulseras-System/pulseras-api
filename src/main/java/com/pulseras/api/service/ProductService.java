package com.pulseras.api.service;

import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAll(String keyword, String categoryId, int page, int size, String sort);
    ProductDto getById(String id);
    void create(CreateProductDto dto);
    void update(String id, CreateProductDto dto);
    void delete(String id);
}
