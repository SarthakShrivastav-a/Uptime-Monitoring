package com.product.uptime.repository;

import com.product.uptime.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, String> {
    List<NotificationChannel> findByUserId(String userId);
}
