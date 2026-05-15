package com.product.uptime.events;

import com.product.uptime.entity.ErrorCondition;

import java.time.Instant;
import java.util.UUID;

public class MonitorLifecycleEvent {
    private String eventId = UUID.randomUUID().toString();
    private String eventType;
    private String monitorId;
    private String userId;
    private String url;
    private ErrorCondition errorCondition;
    private Instant occurredAt = Instant.now();

    public MonitorLifecycleEvent() {
    }

    public MonitorLifecycleEvent(String eventType, String monitorId, String userId, String url, ErrorCondition errorCondition) {
        this.eventType = eventType;
        this.monitorId = monitorId;
        this.userId = userId;
        this.url = url;
        this.errorCondition = errorCondition;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ErrorCondition getErrorCondition() {
        return errorCondition;
    }

    public void setErrorCondition(ErrorCondition errorCondition) {
        this.errorCondition = errorCondition;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
