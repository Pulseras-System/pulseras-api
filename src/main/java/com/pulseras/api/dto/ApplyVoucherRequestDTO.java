package com.pulseras.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyVoucherRequestDTO {
    
    @NotBlank(message = "Voucher code is required")
    private String voucherCode;
    
    @NotBlank(message = "Account ID is required")
    private String accountId;
    
    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0", message = "Order amount cannot be negative")
    private Double orderAmount;
    
    private String orderId; // Optional: for tracking which order used the voucher
}
