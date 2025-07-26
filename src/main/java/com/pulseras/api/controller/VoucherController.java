package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Voucher Management", description = "Shopee-like voucher system with user and admin operations")
public class VoucherController {

    private final VoucherService voucherService;

    // Admin endpoints
    @PostMapping("/admin")
    @Operation(summary = "Create a new voucher", description = "Creates a new voucher with specified parameters (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "409", description = "Voucher code already exists")
    })
    public ResponseEntity<VoucherDTO> createVoucher(@Valid @RequestBody CreateVoucherRequestDTO dto) {
        log.info("Creating new voucher with code: {}", dto.getVoucherCode());
        VoucherDTO voucher = voucherService.createVoucher(dto);
        return ResponseEntity.ok(voucher);
    }

    @GetMapping("/admin")
    @Operation(summary = "Get all vouchers", description = "Retrieves all vouchers for administrative purposes (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of all vouchers retrieved successfully")
    public ResponseEntity<List<VoucherDTO>> getAllVouchersForAdmin() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchersForAdmin();
        return ResponseEntity.ok(vouchers);
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Delete voucher", description = "Deletes a voucher by ID (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Voucher not found")
    })
    public ResponseEntity<Map<String, Object>> deleteVoucher(
            @Parameter(description = "Voucher ID") @PathVariable String id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Voucher deleted successfully"));
    }

    @GetMapping("/admin/{id}")
    @Operation(summary = "Get voucher by ID", description = "Retrieves a specific voucher by ID (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Voucher not found")
    })
    public ResponseEntity<VoucherDTO> getVoucherById(
            @Parameter(description = "Voucher ID") @PathVariable String id) {
        log.info("Fetching voucher with ID: {}", id);
        VoucherDTO voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(voucher);
    }

    @PutMapping("/admin/{id}")
    @Operation(summary = "Update voucher", description = "Updates an existing voucher (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "404", description = "Voucher not found")
    })
    public ResponseEntity<VoucherDTO> updateVoucher(
            @Parameter(description = "Voucher ID") @PathVariable String id,
            @Valid @RequestBody UpdateVoucherRequestDTO dto) {
        log.info("Updating voucher with ID: {}", id);
        VoucherDTO voucher = voucherService.updateVoucher(id, dto);
        return ResponseEntity.ok(voucher);
    }

    @PatchMapping("/admin/{id}/toggle-status")
    @Operation(summary = "Toggle voucher status", description = "Toggles the active status of a voucher (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Voucher not found")
    })
    public ResponseEntity<VoucherDTO> toggleVoucherStatus(
            @Parameter(description = "Voucher ID") @PathVariable String id) {
        log.info("Toggling status for voucher with ID: {}", id);
        VoucherDTO voucher = voucherService.toggleVoucherStatus(id);
        return ResponseEntity.ok(voucher);
    }

    @PatchMapping("/admin/{id}/quantity")
    @Operation(summary = "Update voucher quantity", description = "Updates the total quantity of a voucher (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity"),
            @ApiResponse(responseCode = "404", description = "Voucher not found")
    })
    public ResponseEntity<VoucherDTO> updateVoucherQuantity(
            @Parameter(description = "Voucher ID") @PathVariable String id,
            @Parameter(description = "New quantity") @RequestParam @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
        log.info("Updating quantity for voucher with ID: {} to {}", id, quantity);
        VoucherDTO voucher = voucherService.updateVoucherQuantity(id, quantity);
        return ResponseEntity.ok(voucher);
    }

    // User endpoints - Shopee-like functionality
    @GetMapping("/available")
    @Operation(summary = "Get available vouchers", description = "Retrieves vouchers available to a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of available vouchers retrieved successfully")
    public ResponseEntity<List<VoucherDisplayDTO>> getAvailableVouchers(
            @Parameter(description = "User account ID for personalized availability") 
            @RequestParam(required = false) String accountId) {
        log.info("Fetching available vouchers for account: {}", accountId);
        List<VoucherDisplayDTO> vouchers = voucherService.getAvailableVouchersForUser(accountId);
        return ResponseEntity.ok(vouchers);
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply voucher", description = "Applies a voucher to an order and calculates discount")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voucher applied successfully or failed with reason"),
            @ApiResponse(responseCode = "400", description = "Validation errors")
    })
    public ResponseEntity<VoucherApplicationResultDTO> applyVoucher(@Valid @RequestBody ApplyVoucherRequestDTO dto) {
        log.info("Applying voucher {} for account: {}", dto.getVoucherCode(), dto.getAccountId());
        VoucherApplicationResultDTO result = voucherService.applyVoucher(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate voucher", description = "Validates if a voucher can be used without actually applying it")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voucher validation result")
    public ResponseEntity<Map<String, Object>> validateVoucherForUser(
            @Parameter(description = "Voucher code to validate") @RequestParam String voucherCode,
            @Parameter(description = "User account ID") @RequestParam String accountId,
            @Parameter(description = "Order amount to validate against") @RequestParam Double orderAmount) {
        log.info("Validating voucher {} for account: {} with amount: {}", voucherCode, accountId, orderAmount);
        Map<String, Object> result = voucherService.validateVoucherForUser(voucherCode, accountId, orderAmount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-vouchers/{accountId}")
    @Operation(summary = "Get user vouchers", description = "Retrieves vouchers specific to a user account with usage status")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "List of user vouchers retrieved successfully")
    public ResponseEntity<List<VoucherDisplayDTO>> getUserVouchers(
            @Parameter(description = "User account ID") @PathVariable String accountId) {
        log.info("Fetching vouchers for account: {}", accountId);
        List<VoucherDisplayDTO> vouchers = voucherService.getUserVouchers(accountId);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/usage-history/{accountId}")
    @Operation(summary = "Get voucher usage history", description = "Retrieves the voucher usage history for a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voucher usage history retrieved successfully")
    public ResponseEntity<List<Map<String, Object>>> getVoucherUsageHistory(
            @Parameter(description = "User account ID") @PathVariable String accountId) {
        log.info("Fetching voucher usage history for account: {}", accountId);
        List<Map<String, Object>> history = voucherService.getVoucherUsageHistory(accountId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}/usable/{accountId}")
    @Operation(summary = "Check voucher usability", description = "Checks if a specific voucher can be used by a specific account")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Usability check result")
    public ResponseEntity<Boolean> isVoucherUsable(
            @Parameter(description = "Voucher ID") @PathVariable String id,
            @Parameter(description = "User account ID") @PathVariable String accountId) {
        return ResponseEntity.ok(voucherService.isVoucherUsable(id, accountId));
    }

}
