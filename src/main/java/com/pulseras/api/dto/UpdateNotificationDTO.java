package com.pulseras.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateNotificationDTO {
    private String accountId;
    private String message;
    private Integer status;
    private LocalDateTime lastEdited;
}
