package com.pulseras.api.mapper;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.entity.Category;
import com.pulseras.api.entity.Product;
import com.pulseras.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public Product toEntity(CreateProductDto dto) {
        Product p = new Product();
        p.setCategoryIds(dto.getCategoryIds());
        p.setProductName(dto.getProductName());
        p.setProductDescription(dto.getProductDescription());
        p.setProductMaterial(dto.getProductMaterial());
        p.setProductImage(dto.getProductImage());
        p.setQuantity(dto.getQuantity());
        p.setType(dto.getType());
        p.setPrice(dto.getPrice());
        p.setStatus(dto.getStatus());
        return p;
    }

    public ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setProductId(p.getProductId());
        dto.setCategoryIds(p.getCategoryIds());
        String categoryNames = p.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .map(Category::getCategoryName)
                        .orElse(""))
                .collect(Collectors.joining(","));
        dto.setCategoryName(categoryNames);
        dto.setProductName(p.getProductName());
        dto.setProductDescription(p.getProductDescription());
        dto.setProductMaterial(p.getProductMaterial());
        dto.setProductImage(p.getProductImage());
        dto.setQuantity(p.getQuantity());
        dto.setType(p.getType());
        dto.setPrice(p.getPrice());
        dto.setCreateDate(p.getCreateDate());
        dto.setLastEdited(p.getLastEdited());
        dto.setStatus(p.getStatus());
        return dto;
    }
}
