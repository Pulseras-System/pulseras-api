package com.pulseras.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendFeedbackDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String userEmail;
    
    @NotBlank(message = "Content is required")
    @Size(min = 3, max = 1000, message = "Content must be between 3 and 1000 characters")
    private String content;
    
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    private String subject;
    
    @Size(max = 50, message = "User name must not exceed 50 characters")
    private String userName;
}
