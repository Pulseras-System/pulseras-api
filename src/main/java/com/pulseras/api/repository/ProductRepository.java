package com.pulseras.api.repository;

import com.pulseras.api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<Product> findByCategoryIdsContaining(String categoryId, Pageable pageable);

    List<Product> findByCategoryIdsContainingAndProductNameContainingIgnoreCase(
            String categoryId,
            String keyword,
            Pageable pageable
    );

    List<Product> findTop6ByOrderByCreateDateDesc();
}
