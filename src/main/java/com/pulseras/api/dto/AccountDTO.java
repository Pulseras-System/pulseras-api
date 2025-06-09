package com.pulseras.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private String id;
    private String fullName;
    private String username;
    private String phone;
    private String email;
    private Integer roleId;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
    private Integer status;
}
