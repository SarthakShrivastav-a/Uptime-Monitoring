package com.product.uptime.repository;

import com.product.uptime.entity.MonitorStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MonitorStatusRepository extends MongoRepository<MonitorStatus,String> {
    Optional<MonitorStatus> findById(String id);
    MonitorStatus findByMonitorId(String monitorId);


    void deleteByMonitorId(String id);
}
