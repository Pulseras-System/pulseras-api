package com.pulseras.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new voucher")
public class CreateVoucherRequestDTO {
    
    @NotBlank(message = "Voucher code is required")
    @Size(min = 3, max = 20, message = "Voucher code must be between 3-20 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Voucher code must contain only uppercase letters and numbers")
    @Schema(description = "Unique voucher code", example = "SAVE20", required = true)
    private String voucherCode;
    
    @NotBlank(message = "Voucher name is required")
    @Size(max = 100, message = "Voucher name must not exceed 100 characters")
    @Schema(description = "Display name of the voucher", example = "Save 20% on Electronics", required = true)
    private String voucherName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Detailed description of the voucher", example = "Get 20% discount on all electronic items")
    private String description;
    
    @NotNull(message = "Total quantity is required")
    @Min(value = 1, message = "Total quantity must be at least 1")
    @Schema(description = "Total number of vouchers available", example = "100", required = true)
    private Integer totalQuantity;
    
    @NotBlank(message = "Discount type is required")
    @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT)$", message = "Discount type must be PERCENTAGE or FIXED_AMOUNT")
    @Schema(description = "Type of discount", allowableValues = {"PERCENTAGE", "FIXED_AMOUNT"}, example = "PERCENTAGE", required = true)
    private String discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.1", message = "Discount value must be greater than 0")
    @Schema(description = "Discount value (percentage: 1-100, fixed amount: VND)", example = "20.0", required = true)
    private Double discountValue;
    
    @DecimalMin(value = "0", message = "Minimum order amount cannot be negative")
    @Schema(description = "Minimum order amount required to use voucher (VND)", example = "50000.0")
    private Double minOrderAmount = 0.0;
    
    @DecimalMin(value = "0", message = "Maximum discount amount cannot be negative")
    @Schema(description = "Maximum discount amount (VND, only for percentage discounts)", example = "100000.0")
    private Double maxDiscountAmount;
    
    @NotNull(message = "Start date is required")
    @Schema(description = "Voucher start date and time (ISO LocalDateTime format)", 
            example = "2025-01-01T00:00:00", 
            type = "string", 
            format = "date-time",
            required = true)
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @Schema(description = "Voucher end date and time (ISO LocalDateTime format)", 
            example = "2025-12-31T23:59:59", 
            type = "string", 
            format = "date-time",
            required = true)
    private LocalDateTime endDate;
    
    @Min(value = 1, message = "Max usage per user must be at least 1")
    @Schema(description = "Maximum number of times a user can use this voucher", example = "1")
    private Integer maxUsagePerUser = 1;
}
