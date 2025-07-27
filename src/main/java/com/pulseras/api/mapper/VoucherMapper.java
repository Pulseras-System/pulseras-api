package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateVoucherRequestDTO;
import com.pulseras.api.dto.VoucherDTO;
import com.pulseras.api.dto.VoucherDisplayDTO;
import com.pulseras.api.entity.Voucher;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class VoucherMapper {

    public static VoucherDTO toDTO(Voucher entity) {
        return VoucherDTO.builder()
                .id(entity.getId().toHexString())
                .voucherCode(entity.getVoucherCode())
                .voucherName(entity.getVoucherName())
                .description(entity.getDescription())
                .totalQuantity(entity.getTotalQuantity())
                .usedQuantity(entity.getUsedQuantity())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minOrderAmount(entity.getMinOrderAmount())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .maxUsagePerUser(entity.getMaxUsagePerUser())
                .isActive(entity.getIsActive())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createDate(entity.getCreateDate())
                .lastEdited(entity.getLastEdited())
                .banReason(entity.getBanReason())
                .build();
    }

    public static VoucherDisplayDTO toDisplayDTO(Voucher entity) {
        return VoucherDisplayDTO.builder()
                .id(entity.getId().toHexString())
                .voucherCode(entity.getVoucherCode())
                .voucherName(entity.getVoucherName())
                .description(entity.getDescription())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minOrderAmount(entity.getMinOrderAmount())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .endDate(entity.getEndDate())
                .remainingQuantity(entity.getTotalQuantity() - entity.getUsedQuantity())
                .build();
    }

    public static Voucher toEntity(CreateVoucherRequestDTO dto) {
        return Voucher.builder()
                .id(new ObjectId())
                .voucherCode(dto.getVoucherCode())
                .voucherName(dto.getVoucherName())
                .description(dto.getDescription())
                .totalQuantity(dto.getTotalQuantity())
                .usedQuantity(0)
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minOrderAmount(dto.getMinOrderAmount())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .maxUsagePerUser(dto.getMaxUsagePerUser() != null ? dto.getMaxUsagePerUser() : 1)
                .isActive(true)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createDate(LocalDateTime.now())
                .lastEdited(LocalDateTime.now())
                .build();
    }
}
