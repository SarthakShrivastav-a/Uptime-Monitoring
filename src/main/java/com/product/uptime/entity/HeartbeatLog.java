package com.product.uptime.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "heartbeat_logs", indexes = {
        @Index(name = "heartbeat_logs_cron_job_id_idx", columnList = "cronJobId")
})
public class HeartbeatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String cronJobId;

    private String userId;

    private String cronJobName;

    private LocalDateTime timestamp;
    private String status; // SUCCESS, MISSED
    private Instant createdAt = Instant.now();
}
