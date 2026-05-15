package com.product.uptime.repository;

import com.product.uptime.entity.MaintenanceWindow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, String> {
    List<MaintenanceWindow> findByUserIdOrderByStartsAtDesc(String userId);
}
