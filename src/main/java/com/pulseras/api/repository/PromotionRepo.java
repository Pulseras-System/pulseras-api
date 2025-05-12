package com.pulseras.api.repository;

import com.pulseras.api.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionRepo extends MongoRepository<Promotion, String> {
}
