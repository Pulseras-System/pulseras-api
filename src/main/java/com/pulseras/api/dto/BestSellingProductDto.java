package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BestSellingProductDto {

    private final ProductDto product;
    private final long soldQuantity;
}
