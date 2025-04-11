package com.product.uptime.repository;

import com.product.uptime.entity.Monitor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MonitorRepository extends MongoRepository<Monitor,String> {

    List<Monitor> findAllByUserId(String id);

    Optional<Monitor> findById(String id);
}
