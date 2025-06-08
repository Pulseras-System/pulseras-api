package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private String id;
    private Integer accountId;
    private String message;
    private Integer status;
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
}
