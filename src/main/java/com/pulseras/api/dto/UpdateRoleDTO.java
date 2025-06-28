package com.pulseras.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateRoleDTO {
    private String roleName;
    private Integer status;
    private LocalDateTime lastEdited;
}
