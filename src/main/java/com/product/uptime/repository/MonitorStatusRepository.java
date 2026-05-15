package com.product.uptime.repository;

import com.product.uptime.entity.MonitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonitorStatusRepository extends JpaRepository<MonitorStatus,String> {
    Optional<MonitorStatus> findById(String id);
    MonitorStatus findByMonitorId(String monitorId);


    void deleteByMonitorId(String id);
}
