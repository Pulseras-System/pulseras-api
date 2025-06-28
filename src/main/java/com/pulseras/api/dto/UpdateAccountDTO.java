package com.pulseras.api.dto;

import lombok.Data;

@Data
public class UpdateAccountDTO {
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private Integer status;
}
