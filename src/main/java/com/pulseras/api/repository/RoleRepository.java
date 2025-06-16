package com.pulseras.api.repository;

import com.pulseras.api.entity.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, ObjectId> {
    @Query("{ 'roleName': { $regex: ?0, $options: 'i' } }")
    Optional<Role> findByRoleName(String roleName);
}
