package com.product.uptime.dto;

import java.time.Instant;

public class MonitorStatusUpdate {
    private String monitorId;
    private String status;          // "UP" or "DOWN"
    private String triggerReason;   // Reason for downtime or "Healthy"
    private Instant checkedAt;      // Timestamp when the check was performed
    private long responseTime;      // Response time in milliseconds

    public MonitorStatusUpdate(String monitorId, String status, int responseTime, String triggerReason, Instant checkedAt) {
        this.monitorId = monitorId;
        this.status = status;
        this.responseTime = responseTime;
        this.triggerReason = triggerReason;
        this.checkedAt = checkedAt;
    }
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
