package com.product.uptime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronJobResponse {
    private String id;
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
}