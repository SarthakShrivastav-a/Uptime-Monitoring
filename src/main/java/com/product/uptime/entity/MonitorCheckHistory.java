package com.product.uptime.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "monitor_check_history")
public class MonitorCheckHistory {
    @Id
    private String id;
    private String monitorId;
    private String status;
    private String triggerReason;
    private Instant checkedAt;

    public MonitorCheckHistory() {}

    public MonitorCheckHistory(String monitorId, String status, String triggerReason, Instant checkedAt) {
        this.monitorId = monitorId;
        this.status = status;
        this.triggerReason = triggerReason;
        this.checkedAt = checkedAt;
    }

    public String getId() {
        return id;
    }

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
}
