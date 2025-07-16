package com.pulseras.api.repository;

import com.pulseras.api.entity.Wishlist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WishlistRepository extends MongoRepository<Wishlist, ObjectId> {
}

