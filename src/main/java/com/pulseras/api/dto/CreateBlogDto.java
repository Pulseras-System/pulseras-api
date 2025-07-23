package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlogDto {
    private String accountId;
    private String title;
    private String content;
    private int status;
}