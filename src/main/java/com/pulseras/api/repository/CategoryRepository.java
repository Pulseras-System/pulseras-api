package com.pulseras.api.repository;

import com.pulseras.api.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByCategoryName(String name);
}
