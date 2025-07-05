package com.pulseras.api.repository;

import com.pulseras.api.entity.Voucher;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends MongoRepository<Voucher, ObjectId> {
    List<Voucher> findByAccountId(String accountId);
    Optional<Voucher> findByIdAndAccountId(ObjectId id, String accountId);
    List<Voucher> findByAccountIdAndStatus(String accountId, Integer status);
    List<Voucher> findByStatus(Integer status);
}
