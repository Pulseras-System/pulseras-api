package com.pulseras.api.repository;

import com.pulseras.api.entity.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, ObjectId> {
    List<Order> findByAccountId(String accountId);
    List<Order> findByCreateDateBetween(LocalDateTime start, LocalDateTime end);
//    int countByAccountId(String accountId);
}
