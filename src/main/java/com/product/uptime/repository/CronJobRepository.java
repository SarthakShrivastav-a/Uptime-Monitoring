package com.product.uptime.repository;

import com.product.uptime.entity.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CronJobRepository extends JpaRepository<CronJob, String> {
    Optional<CronJob> findByApiKey(String apiKey);
    List<CronJob> findByActiveTrue();

    List<CronJob> findByUserId(String userId);
    Optional<CronJob> findByUserIdAndId(String userId, String id);
    Optional<CronJob> findByUserIdAndName(String userId, String name);
    List<CronJob> findByUserIdAndActiveTrue(String userId);
}