package com.pulseras.api.service.impl;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.entity.Product;
import com.pulseras.api.entity.Category;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.ProductMapper;
import com.pulseras.api.repository.CategoryRepository;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.service.CategoryService;
import com.pulseras.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;


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

    @Override
    public List<Map<String, Object>> getProductTypeDistribution() {
        List<Product> products = repository.findAll()
                .stream()
                .filter(p -> p.getStatus() != 0)
                .collect(Collectors.toList());

        // Đếm số lượng theo categoryId
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Product product : products) {
            for (String categoryId : product.getCategoryIds()) {
                categoryCountMap.merge(categoryId, 1, Integer::sum);
            }
        }

        // Map từ categoryId -> categoryName
        Map<String, String> categoryNameMap = categoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getCategoryName));

        // Trả về list map {"label": categoryName, "value": count}
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            String categoryId = entry.getKey();
            Integer count = entry.getValue();
            String name = categoryNameMap.getOrDefault(categoryId, "Unknown");

            Map<String, Object> item = new HashMap<>();
            item.put("label", name);
            item.put("value", count);
            result.add(item);
        }

        return result;
    }

}
