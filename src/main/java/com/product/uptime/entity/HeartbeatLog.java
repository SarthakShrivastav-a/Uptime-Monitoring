package com.product.uptime.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "heartbeat_logs")
public class HeartbeatLog {
    @Id
    private String id;

    @Indexed
    private String cronJobId;

    private String userId;

    private String cronJobName;

    private LocalDateTime timestamp;
    private String status; // SUCCESS, MISSED
    private Instant createdAt = Instant.now();
}