package com.pulseras.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDTO {
    private String token;
    private String newPassword;
}