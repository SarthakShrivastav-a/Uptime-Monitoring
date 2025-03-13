package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainInfo {
    private Instant domainExpiry;
    private int daysLeft;
    private String status; // ACTIVE/EXPIRING/EXPIRED

    public Instant getDomainExpiry() {
        return domainExpiry;
    }

    public void setDomainExpiry(Instant domainExpiry) {
        this.domainExpiry = domainExpiry;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}