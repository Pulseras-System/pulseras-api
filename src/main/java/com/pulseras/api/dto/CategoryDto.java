package com.pulseras.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDto {

    private String categoryId;
    private String categoryName;
    private int status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
