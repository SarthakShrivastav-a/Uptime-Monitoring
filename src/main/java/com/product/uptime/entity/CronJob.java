package com.product.uptime.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cronjobs", uniqueConstraints = {
        @UniqueConstraint(name = "user_id_name_idx", columnNames = {"userId", "name"})
})
public class CronJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String name;
    private String description;
    private Long expectedIntervalSeconds;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime nextExpectedHeartbeat;
    private Integer gracePeriodMinutes;
    private Boolean active = true;
    private String alertEmail;

    @Column(unique = true)
    private String apiKey;

    private Instant createdAt = Instant.now();
}
