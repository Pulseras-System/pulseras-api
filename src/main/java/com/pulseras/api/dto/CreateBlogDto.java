package com.pulseras.api.dto;

import lombok.Data;

@Data
public class CreateBlogDto {
    private String accountId;
    private String title;
    private String content;
    private int status;
}