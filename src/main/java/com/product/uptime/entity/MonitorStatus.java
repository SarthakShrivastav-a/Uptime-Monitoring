package com.product.uptime.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "monitor_status")
public class MonitorStatus {
    @Id
    private String id;
    private String monitorId;
    private String status;
    private double uptimePercentage;
    private int totalChecks;
    private int downChecks;
    private long cumulativeDowntime;
    private Instant lastChecked;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getUptimePercentage() {
        return uptimePercentage;
    }

    public void setUptimePercentage(double uptimePercentage) {
        this.uptimePercentage = uptimePercentage;
    }

    public int getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(int totalChecks) {
        this.totalChecks = totalChecks;
    }

    public int getDownChecks() {
        return downChecks;
    }

    public void setDownChecks(int downChecks) {
        this.downChecks = downChecks;
    }

    public long getCumulativeDowntime() {
        return cumulativeDowntime;
    }

    public void setCumulativeDowntime(long cumulativeDowntime) {
        this.cumulativeDowntime = cumulativeDowntime;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }
}