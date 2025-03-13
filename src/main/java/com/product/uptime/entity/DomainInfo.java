package com.product.uptime.entity;

import java.time.Instant;


public class DomainInfo {
    private Instant domainExpiry;
    private Instant remindingDate;
    private String status; // ACTIVE/EXPIRING/EXPIRED

    public Instant getDomainExpiry() {
        return domainExpiry;
    }

    public void setDomainExpiry(Instant domainExpiry) {
        this.domainExpiry = domainExpiry;
    }

    public Instant getRemindingDate() {
        return remindingDate;
    }

    public void setRemindingDate(Instant remindingDate) {
        this.remindingDate = remindingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}