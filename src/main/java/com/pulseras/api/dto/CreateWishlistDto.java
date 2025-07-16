package com.pulseras.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWishlistDto {
    @NotBlank
    private String accountId;

    @NotBlank
    private String productId;

    @NotNull
    private Integer status;
}
