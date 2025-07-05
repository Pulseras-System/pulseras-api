package com.pulseras.api.repository;

import com.pulseras.api.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    List<Promotion> findByPromotionNameContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<Promotion> findFirstByProductIdAndStatusOrderByCreateDateDesc(String productId, int status);
}


