package com.product.uptime.repository;

import com.product.uptime.entity.MonitorCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonitorCheckHistoryRepository extends JpaRepository<MonitorCheckHistory, String> {
    List<MonitorCheckHistory> findByMonitorId(String monitorId);
}
