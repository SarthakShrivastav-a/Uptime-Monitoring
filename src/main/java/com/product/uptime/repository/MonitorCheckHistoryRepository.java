package com.product.uptime.repository;

import com.product.uptime.entity.MonitorCheckHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MonitorCheckHistoryRepository extends MongoRepository<MonitorCheckHistory, String> {
    List<MonitorCheckHistory> findByMonitorId(String monitorId);
}
