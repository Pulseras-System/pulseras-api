package com.pulseras.api.repository;

import com.pulseras.api.entity.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByFeedbackInforContainingIgnoreCase(String keyword, Pageable pageable);
}
