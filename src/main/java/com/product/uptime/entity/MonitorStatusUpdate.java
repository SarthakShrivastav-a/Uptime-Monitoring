package com.product.uptime.entity;

import java.time.Instant;

public class MonitorStatusUpdate {
    private String monitorId;
    private String status;          // "UP" or "DOWN"
    private String triggerReason;   // Reason for downtime or "Healthy"
    private Instant checkedAt;      // Timestamp when the check was performed
    private long responseTime;      // Response time in milliseconds

    // Getters
    public String getMonitorId() {
        return monitorId;
    }

    public String getStatus() {
        return status;
    }

    public String getTriggerReason() {
        return triggerReason;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public long getResponseTime() {
        return responseTime;
    }

}
