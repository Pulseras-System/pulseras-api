package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String productId;
    private List<String> categoryIds;
    private String categoryName;
    private String productName;
    private String productDescription;
    private String productMaterial;
    private String productImage;
    private int quantity;
    private String type;
    private BigDecimal price;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
    private int status;

}
