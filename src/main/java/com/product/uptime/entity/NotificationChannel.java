package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification_channels")
public class NotificationChannel {
    @Id
    private String id;
    private String userId;
    private String name;
    private String type;
    private String target;
    private boolean enabled = true;
    private Instant createdAt = Instant.now();
}
