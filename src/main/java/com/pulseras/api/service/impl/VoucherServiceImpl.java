package com.pulseras.api.service.impl;

import com.pulseras.api.dto.*;
import com.pulseras.api.entity.Voucher;
import com.pulseras.api.entity.VoucherUsage;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.VoucherMapper;
import com.pulseras.api.repository.VoucherRepository;
import com.pulseras.api.repository.VoucherUsageRepository;
import com.pulseras.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    @Override
    @Transactional
    public VoucherDTO createVoucher(CreateVoucherRequestDTO dto) {
        log.info("Creating voucher with code: {}", dto.getVoucherCode());
        
        // Check if voucher code already exists
        if (voucherRepository.existsByVoucherCode(dto.getVoucherCode())) {
            throw new IllegalArgumentException("Voucher code already exists: " + dto.getVoucherCode());
        }

        // Validate dates
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Voucher voucher = Voucher.builder()
                .voucherCode(dto.getVoucherCode())
                .voucherName(dto.getVoucherName())
                .description(dto.getDescription())
                .totalQuantity(dto.getTotalQuantity())
                .usedQuantity(0)
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minOrderAmount(dto.getMinOrderAmount())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .maxUsagePerUser(dto.getMaxUsagePerUser())
                .isActive(true)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createDate(LocalDateTime.now())
                .lastEdited(LocalDateTime.now())
                .build();

        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Voucher created successfully with ID: {}", savedVoucher.getId());
        
        return VoucherMapper.toDTO(savedVoucher);
    }

    @Override
    public List<VoucherDTO> getAllVouchersForAdmin() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers.stream()
                .map(VoucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO getVoucherById(String id) {
        Voucher voucher = voucherRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with ID: " + id));
        return VoucherMapper.toDTO(voucher);
    }

    @Override
    @Transactional
    public void deleteVoucher(String id) {
        Voucher voucher = voucherRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with ID: " + id));
        
        voucher.setIsActive(false);
        voucher.setBanReason("Deleted by admin");
        voucher.setLastEdited(LocalDateTime.now());
        voucherRepository.save(voucher);
        
        log.info("Voucher deactivated with ID: {}", id);
    }

    @Override
    public List<VoucherDisplayDTO> getAvailableVouchersForUser(String accountId) {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> availableVouchers = voucherRepository.findAvailableVouchersWithStock(now);
        
        return availableVouchers.stream()
                .map(voucher -> buildVoucherDisplayDTO(voucher, accountId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VoucherApplicationResultDTO applyVoucher(ApplyVoucherRequestDTO dto) {
        log.info("Applying voucher {} for account {}", dto.getVoucherCode(), dto.getAccountId());
        
        // Find voucher
        Voucher voucher = voucherRepository.findByVoucherCode(dto.getVoucherCode())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + dto.getVoucherCode()));

        // Validate voucher application
        Map<String, Object> validation = validateVoucherForUser(dto.getVoucherCode(), dto.getAccountId(), dto.getOrderAmount());
        if (!(Boolean) validation.get("canUse")) {
            return VoucherApplicationResultDTO.builder()
                    .success(false)
                    .message((String) validation.get("message"))
                    .voucherCode(dto.getVoucherCode())
                    .originalAmount(dto.getOrderAmount())
                    .discountAmount(0.0)
                    .finalAmount(dto.getOrderAmount())
                    .build();
        }

        // Calculate discount
        Double discountAmount = calculateDiscount(dto.getVoucherCode(), dto.getOrderAmount());
        Double finalAmount = dto.getOrderAmount() - discountAmount;

        // Create usage record
        VoucherUsage usage = VoucherUsage.builder()
                .accountId(dto.getAccountId())
                .voucherId(voucher.getId().toHexString())
                .orderId(dto.getOrderId())
                .usedAt(LocalDateTime.now())
                .discountAmount(discountAmount)
                .originalAmount(dto.getOrderAmount())
                .finalAmount(finalAmount)
                .build();

        voucherUsageRepository.save(usage);

        // Update voucher used quantity
        voucher.setUsedQuantity(voucher.getUsedQuantity() + 1);
        voucher.setLastEdited(LocalDateTime.now());
        voucherRepository.save(voucher);

        log.info("Voucher {} applied successfully for account {}, discount: {}", 
                dto.getVoucherCode(), dto.getAccountId(), discountAmount);

        return VoucherApplicationResultDTO.builder()
                .success(true)
                .message("Voucher applied successfully")
                .voucherCode(dto.getVoucherCode())
                .originalAmount(dto.getOrderAmount())
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .build();
    }

    @Override
    public Map<String, Object> validateVoucherForUser(String voucherCode, String accountId, Double orderAmount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

            LocalDateTime now = LocalDateTime.now();

            // Check if voucher is active
            if (!voucher.getIsActive()) {
                result.put("canUse", false);
                result.put("message", "Voucher is not active");
                return result;
            }

            // Check if voucher has started
            if (now.isBefore(voucher.getStartDate())) {
                result.put("canUse", false);
                result.put("message", "Voucher is not yet available");
                return result;
            }

            // Check if voucher has expired
            if (now.isAfter(voucher.getEndDate())) {
                result.put("canUse", false);
                result.put("message", "Voucher has expired");
                return result;
            }

            // Check if voucher is out of stock
            if (voucher.getUsedQuantity() >= voucher.getTotalQuantity()) {
                result.put("canUse", false);
                result.put("message", "Voucher is out of stock");
                return result;
            }

            // Check if user has already used this voucher
            if (accountId != null) {
                long userUsageCount = voucherUsageRepository.countByAccountIdAndVoucherId(accountId, voucher.getId().toHexString());
                if (userUsageCount >= voucher.getMaxUsagePerUser()) {
                    result.put("canUse", false);
                    result.put("message", "You have already used this voucher");
                    return result;
                }
            }

            // Check minimum order amount
            if (orderAmount < voucher.getMinOrderAmount()) {
                result.put("canUse", false);
                result.put("message", String.format("Minimum order amount is %.0f", voucher.getMinOrderAmount()));
                return result;
            }

            // Calculate potential discount
            Double discountAmount = calculateDiscount(voucherCode, orderAmount);
            
            result.put("canUse", true);
            result.put("message", "Voucher can be applied");
            result.put("discountAmount", discountAmount);
            result.put("finalAmount", orderAmount - discountAmount);
            
        } catch (Exception e) {
            result.put("canUse", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public List<VoucherDisplayDTO> getUserVouchers(String accountId) {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> allVouchers = voucherRepository.findByIsActiveTrue();
        
        return allVouchers.stream()
                .map(voucher -> buildVoucherDisplayDTO(voucher, accountId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getVoucherUsageHistory(String accountId) {
        List<VoucherUsage> usages = voucherUsageRepository.findByAccountId(accountId);
        
        return usages.stream().map(usage -> {
            Map<String, Object> usageMap = new HashMap<>();
            
            // Get voucher details
            Voucher voucher = voucherRepository.findById(new ObjectId(usage.getVoucherId())).orElse(null);
            
            usageMap.put("id", usage.getId().toHexString());
            usageMap.put("voucherCode", voucher != null ? voucher.getVoucherCode() : "N/A");
            usageMap.put("voucherName", voucher != null ? voucher.getVoucherName() : "N/A");
            usageMap.put("orderId", usage.getOrderId());
            usageMap.put("usedAt", usage.getUsedAt());
            usageMap.put("originalAmount", usage.getOriginalAmount());
            usageMap.put("discountAmount", usage.getDiscountAmount());
            usageMap.put("finalAmount", usage.getFinalAmount());
            
            return usageMap;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean canUserUseVoucher(String voucherCode, String accountId) {
        Map<String, Object> validation = validateVoucherForUser(voucherCode, accountId, 0.0);
        return (Boolean) validation.getOrDefault("canUse", false);
    }

    @Override
    public Double calculateDiscount(String voucherCode, Double orderAmount) {
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        Double discount = 0.0;

        if ("PERCENTAGE".equals(voucher.getDiscountType())) {
            discount = (orderAmount * voucher.getDiscountValue()) / 100.0;
        } else if ("FIXED_AMOUNT".equals(voucher.getDiscountType())) {
            discount = voucher.getDiscountValue();
        }

        // Apply maximum discount cap if set
        if (voucher.getMaxDiscountAmount() != null && discount > voucher.getMaxDiscountAmount()) {
            discount = voucher.getMaxDiscountAmount();
        }

        // Ensure discount doesn't exceed order amount
        if (discount > orderAmount) {
            discount = orderAmount;
        }

        return discount;
    }

    private VoucherDisplayDTO buildVoucherDisplayDTO(Voucher voucher, String accountId) {
        LocalDateTime now = LocalDateTime.now();
        
        boolean isExpired = now.isAfter(voucher.getEndDate());
        boolean isOutOfStock = voucher.getUsedQuantity() >= voucher.getTotalQuantity();
        boolean hasUserUsed = false;
        
        if (accountId != null) {
            hasUserUsed = voucherUsageRepository.existsByAccountIdAndVoucherId(accountId, voucher.getId().toHexString());
        }
        
        String usageStatus;
        boolean canUse = true;
        
        if (isExpired) {
            usageStatus = "EXPIRED";
            canUse = false;
        } else if (isOutOfStock) {
            usageStatus = "OUT_OF_STOCK";
            canUse = false;
        } else if (hasUserUsed) {
            usageStatus = "USED";
            canUse = false;
        } else {
            usageStatus = "AVAILABLE";
        }

        return VoucherDisplayDTO.builder()
                .id(voucher.getId().toHexString())
                .voucherCode(voucher.getVoucherCode())
                .voucherName(voucher.getVoucherName())
                .description(voucher.getDescription())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .totalQuantity(voucher.getTotalQuantity())
                .remainingQuantity(voucher.getTotalQuantity() - voucher.getUsedQuantity())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .canUse(canUse)
                .isExpired(isExpired)
                .isOutOfStock(isOutOfStock)
                .usageStatus(usageStatus)
                .build();
    }
    
    // Legacy methods for backward compatibility
    @Override
    public boolean isVoucherUsable(String voucherId, String accountId) {
        try {
            Optional<Voucher> voucherOpt = voucherRepository.findById(new ObjectId(voucherId));
            if (voucherOpt.isEmpty()) {
                return false;
            }
            
            Voucher voucher = voucherOpt.get();
            
            // Check if voucher is active and not expired
            LocalDateTime now = LocalDateTime.now();
            if (!voucher.getIsActive() || now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
                return false;
            }
            
            // Check if voucher has stock
            if (voucher.getUsedQuantity() >= voucher.getTotalQuantity()) {
                return false;
            }
            
            // Check if user has already used this voucher
            return !voucherUsageRepository.existsByAccountIdAndVoucherId(accountId, voucherId);
            
        } catch (Exception e) {
            log.error("Error checking voucher usability: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public void markVoucherAsUsed(String voucherId, String accountId) {
        try {
            Optional<Voucher> voucherOpt = voucherRepository.findById(new ObjectId(voucherId));
            if (voucherOpt.isEmpty()) {
                throw new RuntimeException("Voucher not found");
            }
            
            Voucher voucher = voucherOpt.get();
            
            // Check if already used
            if (voucherUsageRepository.existsByAccountIdAndVoucherId(accountId, voucherId)) {
                throw new RuntimeException("Voucher already used by this account");
            }
            
            // Create usage record
            VoucherUsage usage = VoucherUsage.builder()
                    .id(new ObjectId())
                    .accountId(accountId)
                    .voucherId(voucherId)
                    .usedAt(LocalDateTime.now())
                    .discountAmount(calculateDiscount(voucher.getVoucherCode(), 0.0)) // This will be updated when actual order amount is known
                    .build();
            
            voucherUsageRepository.save(usage);
            
            // Update voucher used quantity
            voucher.setUsedQuantity(voucher.getUsedQuantity() + 1);
            voucherRepository.save(voucher);
            
            log.info("Marked voucher {} as used by account {}", voucherId, accountId);
            
        } catch (Exception e) {
            log.error("Error marking voucher as used: {}", e.getMessage());
            throw new RuntimeException("Failed to mark voucher as used: " + e.getMessage());
        }
    }

    @Override
    public VoucherDTO updateVoucher(String id, UpdateVoucherRequestDTO dto) {
        log.info("Updating voucher with ID: {}", id);
        
        // Find existing voucher
        Optional<Voucher> voucherOpt = voucherRepository.findById(new ObjectId(id));
        if (voucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher not found with ID: " + id);
        }
        
        Voucher voucher = voucherOpt.get();
        
        // Update fields
        voucher.setVoucherName(dto.getVoucherName());
        voucher.setDescription(dto.getDescription());
        voucher.setTotalQuantity(dto.getTotalQuantity());
        voucher.setDiscountType(dto.getDiscountType());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setMinOrderAmount(dto.getMinOrderAmount());
        voucher.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setMaxUsagePerUser(dto.getMaxUsagePerUser());
        voucher.setIsActive(dto.getIsActive());
        voucher.setBanReason(dto.getBanReason());
        voucher.setLastEdited(LocalDateTime.now());
        
        // Save updated voucher
        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Updated voucher: {}", savedVoucher.getVoucherCode());
        
        return VoucherMapper.toDTO(savedVoucher);
    }

    @Override
    public VoucherDTO toggleVoucherStatus(String id) {
        log.info("Toggling status for voucher with ID: {}", id);
        
        Optional<Voucher> voucherOpt = voucherRepository.findById(new ObjectId(id));
        if (voucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher not found with ID: " + id);
        }
        
        Voucher voucher = voucherOpt.get();
        boolean newStatus = !voucher.getIsActive();
        voucher.setIsActive(newStatus);
        voucher.setLastEdited(LocalDateTime.now());
        
        // Set ban reason if deactivating
        if (!newStatus && voucher.getBanReason() == null) {
            voucher.setBanReason("Manually deactivated by admin");
        } else if (newStatus) {
            voucher.setBanReason(null); // Clear ban reason when reactivating
        }
        
        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Toggled voucher {} status to: {}", savedVoucher.getVoucherCode(), newStatus);
        
        return VoucherMapper.toDTO(savedVoucher);
    }

    @Override
    public VoucherDTO updateVoucherQuantity(String id, Integer newQuantity) {
        log.info("Updating quantity for voucher with ID: {} to {}", id, newQuantity);
        
        Optional<Voucher> voucherOpt = voucherRepository.findById(new ObjectId(id));
        if (voucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher not found with ID: " + id);
        }
        
        Voucher voucher = voucherOpt.get();
        
        // Validate that new quantity is not less than already used quantity
        if (newQuantity < voucher.getUsedQuantity()) {
            throw new RuntimeException("New quantity (" + newQuantity + ") cannot be less than already used quantity (" + voucher.getUsedQuantity() + ")");
        }
        
        voucher.setTotalQuantity(newQuantity);
        voucher.setLastEdited(LocalDateTime.now());
        
        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Updated voucher {} quantity to: {}", savedVoucher.getVoucherCode(), newQuantity);
        
        return VoucherMapper.toDTO(savedVoucher);
    }
}
