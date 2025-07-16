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
public class WishlistDto {
    private String wishlistId;
    private String accountId;
    private String fullName;
    private String productId;
    private String productName;
    private int status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
