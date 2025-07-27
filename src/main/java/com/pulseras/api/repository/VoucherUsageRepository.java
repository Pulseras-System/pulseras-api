package com.pulseras.api.repository;

import com.pulseras.api.entity.VoucherUsage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherUsageRepository extends MongoRepository<VoucherUsage, ObjectId> {
    
    // Check if user has already used a specific voucher
    boolean existsByAccountIdAndVoucherId(String accountId, String voucherId);
    
    // Get all voucher usages for a specific user
    List<VoucherUsage> findByAccountId(String accountId);
    
    // Get all usages for a specific voucher
    List<VoucherUsage> findByVoucherId(String voucherId);
    
    // Count how many times a user has used a specific voucher
    long countByAccountIdAndVoucherId(String accountId, String voucherId);
    
    // Get specific usage record
    Optional<VoucherUsage> findByAccountIdAndVoucherId(String accountId, String voucherId);
    
    // Count total usage for a voucher
    long countByVoucherId(String voucherId);
}
