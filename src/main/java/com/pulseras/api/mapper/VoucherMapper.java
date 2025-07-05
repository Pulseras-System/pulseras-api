package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;
import com.pulseras.api.entity.Voucher;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class VoucherMapper {

    public static VoucherDTO toDTO(Voucher entity) {
        return VoucherDTO.builder()
                .id(entity.getId().toHexString())
                .voucherName(entity.getVoucherName())
                .accountId(entity.getAccountId())
                .usedByAccounts(entity.getUsedByAccounts())
                .voucherQuantity(entity.getVoucherQuantity())
                .minPrice(entity.getMinPrice())
                .maxDiscount(entity.getMaxDiscount())
                .discountPercentage(entity.getDiscountPercentage())
                .startDay(entity.getStartDay())
                .expireDay(entity.getExpireDay())
                .status(entity.getStatus())
                .createDate(entity.getCreateDate())
                .lastEdited(entity.getLastEdited())
                .build();
    }

    public static Voucher toEntity(CreateVoucherDTO dto) {
        return Voucher.builder()
                .id(new ObjectId())
                .voucherName(dto.getVoucherName())
                .accountId(dto.getAccountId())
                .voucherQuantity(dto.getVoucherQuantity())
                .minPrice(dto.getMinPrice())
                .maxDiscount(dto.getMaxDiscount())
                .discountPercentage(dto.getDiscountPercentage())
                .startDay(dto.getStartDay())
                .expireDay(dto.getExpireDay())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createDate(LocalDateTime.now())
                .build();
    }
}
