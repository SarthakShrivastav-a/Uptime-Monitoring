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
    private long consecutiveDowntimeCount = 0;
    private long cumulativeResponse;
    private double averageResponseTime;
    private Instant lastChecked;
    private int upChecks;


    public MonitorStatus() {
        this.status = "UNKNOWN";
        this.uptimePercentage = 0.0;
        this.totalChecks = 0;
        this.downChecks = 0;
        this.cumulativeDowntime = 0;
        this.consecutiveDowntimeCount = 0;
        this.cumulativeResponse = 0;
        this.averageResponseTime = 0.0;
        this.upChecks = 0;
        this.lastChecked = Instant.now();
    }

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

    public long getConsecutiveDowntimeCount() {
        return consecutiveDowntimeCount;
    }

    public void setConsecutiveDowntimeCount(long consecutiveDowntimeCount) {
        this.consecutiveDowntimeCount = consecutiveDowntimeCount;
    }

    public long getCumulativeResponse() {
        return cumulativeResponse;
    }

    public void setCumulativeResponse(long cumulativeResponse) {
        this.cumulativeResponse = cumulativeResponse;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }

    public int getUpChecks() {
        return upChecks;
    }

    public void setUpChecks(int upChecks) {
        this.upChecks = upChecks;
    }
}
