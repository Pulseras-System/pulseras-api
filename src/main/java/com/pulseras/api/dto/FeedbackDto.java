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
public class FeedbackDto {

    private String feedbackId;
    private String accountId;
    private String productId;
    private String productName;
    private String fullName;
    private String feedbackInfor;
    private int status;
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
}