package com.pulseras.api.repository;

import com.pulseras.api.entity.Wishlist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WishlistRepository extends MongoRepository<Wishlist, ObjectId> {
    List<Wishlist> findByAccountId(ObjectId accountId);

}

