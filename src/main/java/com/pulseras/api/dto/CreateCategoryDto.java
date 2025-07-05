package com.pulseras.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryDto {

    @NotBlank
    private String categoryName;

    @NotNull
    private Integer status;
}
