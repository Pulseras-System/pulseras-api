package com.pulseras.api.service.impl;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.entity.Product;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.ProductMapper;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> getAll(String keyword, String categoryId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Product> result;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = categoryId != null && !categoryId.isEmpty();

        if (hasCategory && hasKeyword) {
            result = new PageImpl<>(
                    repository.findByCategoryIdsContainingAndProductNameContainingIgnoreCase(categoryId, keyword, pageable)
            );
        } else if (hasCategory) {
            result = new PageImpl<>(
                    repository.findByCategoryIdsContaining(categoryId, pageable)
            );
        } else if (hasKeyword) {
            result = repository.findByProductNameContainingIgnoreCase(keyword, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        List<ProductDto> content = result.getContent().stream()
                .map(ProductMapper::toDto)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }

    @Override
    public ProductDto getById(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductMapper.toDto(product);
    }

    @Override
    public void create(CreateProductDto dto) {
        Product entity = ProductMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public void update(String id, CreateProductDto dto) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setCategoryIds(dto.getCategoryIds());
        existing.setProductName(dto.getProductName());
        existing.setProductDescription(dto.getProductDescription());
        existing.setProductMaterial(dto.getProductMaterial());
        existing.setProductImage(dto.getProductImage());
        existing.setQuantity(dto.getQuantity());
        existing.setType(dto.getType());
        existing.setPrice(dto.getPrice());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());

        repository.save(existing);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        repository.deleteById(id);
    }
}
