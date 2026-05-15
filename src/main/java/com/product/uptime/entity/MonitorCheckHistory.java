package com.product.uptime.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "monitor_check_history")
public class MonitorCheckHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
