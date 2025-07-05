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
import java.util.ArrayList;
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
        existing.setAccountId(dto.getAccountId());
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
        if (dto.getAccountId() != null) existing.setAccountId(dto.getAccountId());
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

    @Override
    public List<VoucherDTO> getVouchersByAccountId(String accountId) {
        return repository.findByAccountId(accountId).stream()
                .map(VoucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO getVoucherByIdAndAccountId(String id, String accountId) {
        Voucher voucher = repository.findByIdAndAccountId(new ObjectId(id), accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id + " for account: " + accountId));
        return VoucherMapper.toDTO(voucher);
    }

    @Override
    public void markVoucherAsUsed(String voucherId, String usedByAccountId) {
        Voucher voucher = repository.findById(new ObjectId(voucherId))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + voucherId));
        
        // Initialize the list if it's null
        if (voucher.getUsedByAccounts() == null) {
            voucher.setUsedByAccounts(new ArrayList<>());
        }
        
        // Add the account to the used list if not already present
        if (!voucher.getUsedByAccounts().contains(usedByAccountId)) {
            voucher.getUsedByAccounts().add(usedByAccountId);
            voucher.setLastEdited(LocalDateTime.now());
            repository.save(voucher);
        }
    }

    @Override
    public boolean isVoucherUsable(String voucherId, String accountId) {
        try {
            Voucher voucher = repository.findById(new ObjectId(voucherId))
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + voucherId));
            
            // Check if voucher belongs to the account (owner can always use their own vouchers)
            // OR if it's a public voucher (accountId is null or empty)
            boolean isOwner = accountId.equals(voucher.getAccountId());
            boolean isPublicVoucher = voucher.getAccountId() == null || voucher.getAccountId().trim().isEmpty();
            
            if (!isOwner && !isPublicVoucher) {
                return false; // Not owner and not public voucher
            }
            
            // Check if this account has already used this voucher
            if (voucher.getUsedByAccounts() != null && voucher.getUsedByAccounts().contains(accountId)) {
                return false; // Already used by this account
            }
            
            // Check if voucher is active
            if (voucher.getStatus() == null || voucher.getStatus() != 1) {
                return false;
            }
            
            // Check if voucher is within valid date range
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartDay() != null && now.isBefore(voucher.getStartDay())) {
                return false;
            }
            if (voucher.getExpireDay() != null && now.isAfter(voucher.getExpireDay())) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<VoucherDTO> getAvailableVouchersByAccountId(String accountId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get vouchers owned by the account OR public vouchers (accountId is null/empty)
        List<Voucher> ownedVouchers = repository.findByAccountIdAndStatus(accountId, 1);
        List<Voucher> publicVouchers = repository.findByStatus(1).stream()
                .filter(voucher -> voucher.getAccountId() == null || voucher.getAccountId().trim().isEmpty())
                .toList();
        
        // Combine both lists
        List<Voucher> allAvailableVouchers = new ArrayList<>();
        allAvailableVouchers.addAll(ownedVouchers);
        allAvailableVouchers.addAll(publicVouchers);
        
        return allAvailableVouchers.stream()
                .filter(voucher -> {
                    // Check if account hasn't used this voucher yet
                    boolean notUsedByAccount = voucher.getUsedByAccounts() == null || 
                                             !voucher.getUsedByAccounts().contains(accountId);
                    
                    // Check date validity
                    boolean dateValid = (voucher.getStartDay() == null || !now.isBefore(voucher.getStartDay())) &&
                                       (voucher.getExpireDay() == null || !now.isAfter(voucher.getExpireDay()));
                    
                    return notUsedByAccount && dateValid;
                })
                .map(VoucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> getPublicVouchers() {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByStatus(1).stream()
                .filter(voucher -> 
                    // Only public vouchers (no accountId or empty)
                    (voucher.getAccountId() == null || voucher.getAccountId().trim().isEmpty()) &&
                    // Check date validity
                    (voucher.getStartDay() == null || !now.isBefore(voucher.getStartDay())) &&
                    (voucher.getExpireDay() == null || !now.isAfter(voucher.getExpireDay()))
                )
                .map(VoucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAccountUsedVoucher(String voucherId, String accountId) {
        try {
            Voucher voucher = repository.findById(new ObjectId(voucherId))
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + voucherId));
            
            return voucher.getUsedByAccounts() != null && voucher.getUsedByAccounts().contains(accountId);
        } catch (Exception e) {
            return false;
        }
    }

}
