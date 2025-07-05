package com.pulseras.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    private String ratingId;
    private String accountId;
    private String productId;
    private String productName;
    private String comment;
    private int rating;
    private int status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
