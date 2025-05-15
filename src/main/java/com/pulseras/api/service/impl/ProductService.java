package com.pulseras.api.service.impl;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.entity.Product;
import com.pulseras.api.mapper.ProductMapper;
import com.pulseras.api.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    public List<ProductDto> findAll() {
        return repo.findAll().stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }

    public ProductDto findById(String id) {
        return repo.findById(id).map(ProductMapper::toDto).orElse(null);
    }

    public ProductDto create(ProductDto dto) {
        Product saved = repo.save(ProductMapper.toEntity(dto));
        return ProductMapper.toDto(saved);
    }

    public ProductDto update(String id, ProductDto dto) {
        Product existing = repo.findById(id).orElseThrow();
        Product updated = ProductMapper.toEntity(dto);
        updated.setId(id); // ensure same ID
        return ProductMapper.toDto(repo.save(updated));
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
