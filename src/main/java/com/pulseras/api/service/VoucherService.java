
package com.pulseras.api.service;

import com.pulseras.api.dto.*;

import java.util.List;
import java.util.Map;

public interface VoucherService {
    
    // Admin operations - Full CRUD
    VoucherDTO createVoucher(CreateVoucherRequestDTO dto);
    List<VoucherDTO> getAllVouchersForAdmin();
    VoucherDTO getVoucherById(String id);
    VoucherDTO updateVoucher(String id, UpdateVoucherRequestDTO dto);
    void deleteVoucher(String id);
    VoucherDTO toggleVoucherStatus(String id);
    VoucherDTO updateVoucherQuantity(String id, Integer newQuantity);
    
    // User operations - Shopee-like
    List<VoucherDisplayDTO> getAvailableVouchersForUser(String accountId);
    VoucherApplicationResultDTO applyVoucher(ApplyVoucherRequestDTO dto);
    Map<String, Object> validateVoucherForUser(String voucherCode, String accountId, Double orderAmount);
    List<VoucherDisplayDTO> getUserVouchers(String accountId);
    List<Map<String, Object>> getVoucherUsageHistory(String accountId);
    
    // Internal operations
    boolean canUserUseVoucher(String voucherCode, String accountId);
    Double calculateDiscount(String voucherCode, Double orderAmount);
    
    // Legacy methods for backward compatibility
    boolean isVoucherUsable(String voucherId, String accountId);
    void markVoucherAsUsed(String voucherId, String accountId);
}
