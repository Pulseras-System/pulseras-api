package com.pulseras.api.mapper;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductDto dto) {
        Product p = new Product();
        p.setId(dto.id);
        p.setCategoryId(dto.categoryId);
        p.setProductName(dto.productName);
        p.setProductDescription(dto.productDescription);
        p.setProductMaterial(dto.productMaterial);
        p.setProductImage(dto.productImage);
        p.setQuantity(dto.quantity);
        p.setType(dto.type);
        p.setStatus(dto.status);
        return p;
    }

    public static ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.id = p.getId();
        dto.categoryId = p.getCategoryId();
        dto.productName = p.getProductName();
        dto.productDescription = p.getProductDescription();
        dto.productMaterial = p.getProductMaterial();
        dto.productImage = p.getProductImage();
        dto.quantity = p.getQuantity();
        dto.type = p.getType();
        dto.status = p.getStatus();
        return dto;
    }
}
