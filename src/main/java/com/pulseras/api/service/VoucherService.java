package com.pulseras.api.service;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.UpdateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;

import java.util.List;

public interface VoucherService {
    List<VoucherDTO> getAllVouchers();
    VoucherDTO getVoucherById(String id);
    VoucherDTO createVoucher(CreateVoucherDTO dto);
    VoucherDTO updateVoucher(String id, CreateVoucherDTO dto);
    void deleteVoucher(String id);
    VoucherDTO partialUpdateVoucher(String id, UpdateVoucherDTO dto);
}
