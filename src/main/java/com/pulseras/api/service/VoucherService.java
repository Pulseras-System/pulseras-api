package com.pulseras.api.service;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.UpdateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;

import java.util.List;

public interface VoucherService {
    List<VoucherDTO> getAllVouchers();
    List<VoucherDTO> getVouchersByAccountId(String accountId);
    List<VoucherDTO> getAvailableVouchersByAccountId(String accountId);
    List<VoucherDTO> getPublicVouchers();
    VoucherDTO getVoucherById(String id);
    VoucherDTO getVoucherByIdAndAccountId(String id, String accountId);
    VoucherDTO createVoucher(CreateVoucherDTO dto);
    VoucherDTO updateVoucher(String id, CreateVoucherDTO dto);
    void deleteVoucher(String id);
    VoucherDTO partialUpdateVoucher(String id, UpdateVoucherDTO dto);
    void markVoucherAsUsed(String voucherId, String usedByAccountId);
    boolean isVoucherUsable(String voucherId, String accountId);
    boolean hasAccountUsedVoucher(String voucherId, String accountId);
}
