package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.UpdateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.VoucherMapper;
import com.pulseras.api.entity.Voucher;
import com.pulseras.api.repository.VoucherRepository;
import com.pulseras.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository repository;

    @Override
    public List<VoucherDTO> getAllVouchers() {
        return repository.findAll().stream()
                .map(VoucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO getVoucherById(String id) {
        Voucher voucher = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));
        return VoucherMapper.toDTO(voucher);
    }

    @Override
    public VoucherDTO createVoucher(CreateVoucherDTO dto) {
        Voucher entity = VoucherMapper.toEntity(dto);
        return VoucherMapper.toDTO(repository.save(entity));
    }

    @Override
    public VoucherDTO updateVoucher(String id, CreateVoucherDTO dto) {
        Voucher existing = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        existing.setVoucherName(dto.getVoucherName());
        existing.setVoucherQuantity(dto.getVoucherQuantity());
        existing.setMinPrice(dto.getMinPrice());
        existing.setMaxDiscount(dto.getMaxDiscount());
        existing.setDiscountPercentage(dto.getDiscountPercentage());
        existing.setStartDay(dto.getStartDay());
        existing.setExpireDay(dto.getExpireDay());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        return VoucherMapper.toDTO(repository.save(existing));
    }

    @Override
    public void deleteVoucher(String id) {
        ObjectId objId = new ObjectId(id);
        if (!repository.existsById(objId)) {
            throw new ResourceNotFoundException("Voucher not found with id: " + id);
        }
        repository.deleteById(objId);
    }
    @Override
    public VoucherDTO partialUpdateVoucher(String id, UpdateVoucherDTO dto) {
        Voucher existing = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (dto.getVoucherName() != null) existing.setVoucherName(dto.getVoucherName());
        if (dto.getVoucherQuantity() != null) existing.setVoucherQuantity(dto.getVoucherQuantity());
        if (dto.getMinPrice() != null) existing.setMinPrice(dto.getMinPrice());
        if (dto.getMaxDiscount() != null) existing.setMaxDiscount(dto.getMaxDiscount());
        if (dto.getDiscountPercentage() != null) existing.setDiscountPercentage(dto.getDiscountPercentage());
        if (dto.getStartDay() != null) existing.setStartDay(dto.getStartDay());
        if (dto.getExpireDay() != null) existing.setExpireDay(dto.getExpireDay());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited() != null ? dto.getLastEdited() : LocalDateTime.now());

        return VoucherMapper.toDTO(repository.save(existing));
    }

}
