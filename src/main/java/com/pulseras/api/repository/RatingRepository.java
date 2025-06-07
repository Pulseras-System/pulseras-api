package com.pulseras.api.repository;

import com.pulseras.api.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByCommentContainingIgnoreCase(String keyword, Pageable pageable);
}
