package com.product.uptime.repository;

import com.product.uptime.entity.MaintenanceWindow;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MaintenanceWindowRepository extends MongoRepository<MaintenanceWindow, String> {
    List<MaintenanceWindow> findByUserIdOrderByStartsAtDesc(String userId);
}
