package com.product.uptime.repository;

import com.product.uptime.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, String> {
    List<Incident> findByUserIdOrderByCreatedAtDesc(String userId);
}
