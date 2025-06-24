package com.pulseras.api.repository;

import com.pulseras.api.entity.PasswordResetToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, ObjectId> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByAccountId(ObjectId accountId);
}