package com.product.uptime.repository;

import com.product.uptime.entity.Monitor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MonitorRepository extends MongoRepository<Monitor,String> {
}
