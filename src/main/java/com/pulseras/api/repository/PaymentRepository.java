package com.pulseras.api.repository;

import com.pulseras.api.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByOrderCode(Long orderCode);
    boolean existsByReferenceAndStatus(String reference, String status);
}
