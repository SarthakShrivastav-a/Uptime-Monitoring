package com.product.uptime.repository;

import com.product.uptime.entity.MonitorStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MonitorStatusRepository extends MongoRepository<MonitorStatus,String> {
}
