package com.product.uptime.repository;

import com.product.uptime.entity.CronJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CronJobRepository extends MongoRepository<CronJob, String> {
    Optional<CronJob> findByApiKey(String apiKey);
    List<CronJob> findByActiveTrue();

    List<CronJob> findByUserId(String userId);
    Optional<CronJob> findByUserIdAndId(String userId, String id);
    Optional<CronJob> findByUserIdAndName(String userId, String name);
    List<CronJob> findByUserIdAndActiveTrue(String userId);
}