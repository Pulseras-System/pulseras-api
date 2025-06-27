package com.pulseras.api.repository;

import com.pulseras.api.entity.Account;
import com.pulseras.api.entity.Order;
import com.pulseras.api.entity.PasswordResetToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, ObjectId> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Account> findByCreateDateBetween(LocalDateTime start, LocalDateTime end);
    List<Account> findAllByRoleId(String roleId);
}
