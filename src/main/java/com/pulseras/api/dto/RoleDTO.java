package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private String id;
    private String roleName;
    private Integer status;
    private LocalDateTime createdDate;
    private LocalDateTime lastEdited;
}
