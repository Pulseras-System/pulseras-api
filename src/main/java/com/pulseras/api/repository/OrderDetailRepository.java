package com.pulseras.api.repository;

import com.pulseras.api.entity.OrderDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends MongoRepository<OrderDetail, ObjectId> {
    Optional<OrderDetail> findFirstByOrderId(String orderId);
    List<OrderDetail> findByOrderId(String orderId);
}
