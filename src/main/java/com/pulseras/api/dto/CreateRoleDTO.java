package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoleDTO {
    private String roleName;
    private Integer status;
    private LocalDateTime lastEdited;
}
