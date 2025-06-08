package com.pulseras.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountDTO {
    private String fullName;
    private String password;
    private String username;
    private String phone;
    private String email;
    private Integer roleId;
    private Integer status;
}
