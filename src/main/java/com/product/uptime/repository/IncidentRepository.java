package com.product.uptime.repository;

import com.product.uptime.entity.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IncidentRepository extends MongoRepository<Incident, String> {
    List<Incident> findByUserIdOrderByCreatedAtDesc(String userId);
}
