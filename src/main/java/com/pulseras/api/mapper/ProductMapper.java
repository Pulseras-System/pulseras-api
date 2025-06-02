package com.pulseras.api.mapper;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.entity.Product;

public class ProductMapper {

    public static Product toEntity(CreateProductDto dto) {
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

    public static ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setProductId(p.getProductId());
        dto.setCategoryIds(p.getCategoryIds());
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
