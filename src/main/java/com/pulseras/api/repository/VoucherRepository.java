package com.pulseras.api.repository;

import com.pulseras.api.entity.Voucher;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends MongoRepository<Voucher, ObjectId> {
    
    // Find voucher by unique code
    Optional<Voucher> findByVoucherCode(String voucherCode);
    
    // Find all active vouchers
    List<Voucher> findByIsActiveTrue();
    
    // Find active vouchers that haven't expired
    @Query("{ 'isActive': true, 'endDate': { $gte: ?0 }, 'startDate': { $lte: ?0 } }")
    List<Voucher> findAvailableVouchers(LocalDateTime currentTime);
    
    // Find vouchers that still have quantity available
    @Query("{ 'isActive': true, 'endDate': { $gte: ?0 }, 'startDate': { $lte: ?0 }, $expr: { $gt: ['$totalQuantity', '$usedQuantity'] } }")
    List<Voucher> findAvailableVouchersWithStock(LocalDateTime currentTime);
    
    // Check if voucher code already exists
    boolean existsByVoucherCode(String voucherCode);
}
