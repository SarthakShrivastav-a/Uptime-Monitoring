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
    private double uptime;
    private int downtime;
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

    public double getUptime() {
        return uptime;
    }

    public void setUptime(double uptime) {
        this.uptime = uptime;
    }

    public int getDowntime() {
        return downtime;
    }

    public void setDowntime(int downtime) {
        this.downtime = downtime;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }
}