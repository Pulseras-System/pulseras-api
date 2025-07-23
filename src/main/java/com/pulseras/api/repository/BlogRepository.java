package com.pulseras.api.repository;

import com.pulseras.api.entity.Blog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogRepository extends MongoRepository<Blog, ObjectId> {
    List<Blog> findByAccountId(ObjectId accountId);
    List<Blog> findTop5ByStatusOrderByCreateDateDesc(int status);
}
