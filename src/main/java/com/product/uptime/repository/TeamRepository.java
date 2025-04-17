package com.product.uptime.repository;

import com.product.uptime.entity.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
    Optional<Team> findByUserId(String userId);
}