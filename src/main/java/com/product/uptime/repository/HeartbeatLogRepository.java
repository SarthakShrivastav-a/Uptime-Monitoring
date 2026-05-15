package com.product.uptime.repository;

import com.product.uptime.entity.HeartbeatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HeartbeatLogRepository extends JpaRepository<HeartbeatLog, String> {
    List<HeartbeatLog> findByCronJobIdOrderByTimestampDesc(String cronJobId);
    List<HeartbeatLog> findByUserIdOrderByTimestampDesc(String userId);
    List<HeartbeatLog> findByUserIdAndCronJobIdOrderByTimestampDesc(String userId, String cronJobId);
    void deleteByCronJobId(String cronJobId);
}