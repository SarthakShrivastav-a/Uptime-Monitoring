package com.product.uptime.repository;

import com.product.uptime.entity.HeartbeatLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HeartbeatLogRepository extends MongoRepository<HeartbeatLog, String> {
    List<HeartbeatLog> findByCronJobIdOrderByTimestampDesc(String cronJobId);
    List<HeartbeatLog> findByUserIdOrderByTimestampDesc(String userId);
    List<HeartbeatLog> findByUserIdAndCronJobIdOrderByTimestampDesc(String userId, String cronJobId);
    void deleteByCronJobId(String cronJobId);
}