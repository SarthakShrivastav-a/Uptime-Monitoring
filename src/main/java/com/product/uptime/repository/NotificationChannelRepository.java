package com.product.uptime.repository;

import com.product.uptime.entity.NotificationChannel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationChannelRepository extends MongoRepository<NotificationChannel, String> {
    List<NotificationChannel> findByUserId(String userId);
}
