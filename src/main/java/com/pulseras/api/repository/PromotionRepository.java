package com.pulseras.api.repository;

import com.pulseras.api.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    List<Promotion> findByPromotionNameContainingIgnoreCase(String keyword, Pageable pageable);
}
