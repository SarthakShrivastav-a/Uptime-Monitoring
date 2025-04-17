package com.product.uptime.dto;

import com.product.uptime.entity.HeartbeatLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronJobDetailsDTO {
    private String id;
    private String userId;
    private String name;
    private String description;
    private Long expectedIntervalSeconds;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime nextExpectedHeartbeat;
    private Integer gracePeriodMinutes;
    private Boolean active;
    private String alertEmail;
    private String apiKey;
    private String heartbeatUrl;
    private String currentStatus;
    private double uptimePercentage;
    private long totalChecks;
    private long successChecks;
    private long missedChecks;
    private java.util.List<HeartbeatLog> heartbeatHistory;
}