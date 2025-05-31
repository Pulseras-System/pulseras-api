package com.pulseras.api.repository;

import com.pulseras.api.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String>  {
    Optional<Account> findByUsername(String username);

    List<Account> findByRoleId(int roleId);

    List<Account> findByStatus(int status);

    List<Account> findByFullNameContainingIgnoreCase(String keyword);
}
