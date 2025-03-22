package com.product.uptime.entity;

import java.time.Instant;

public class MonitorStatusUpdate {
    private String monitorId;
    private String url;
    private String triggerReason;
    private Instant timestamp;

    public String getMonitorId() {
        return monitorId;
    }

    public String getUrl() {
        return url;
    }

    public String getTriggerReason() {
        return triggerReason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
