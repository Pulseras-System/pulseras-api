package com.pulseras.api.repository;

import com.pulseras.api.entity.Voucher;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends MongoRepository<Voucher, ObjectId> {
}
