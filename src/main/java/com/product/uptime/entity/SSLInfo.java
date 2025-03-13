package com.product.uptime.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


public class SSLInfo {
    private String certificateIssuer;
    private Instant certificateExpiry;
    private Instant remindingDate;
    private String status; // VALID/EXPIRING/EXPIRED

    public String getCertificateIssuer() {
        return certificateIssuer;
    }

    public void setCertificateIssuer(String certificateIssuer) {
        this.certificateIssuer = certificateIssuer;
    }

    public Instant getCertificateExpiry() {
        return certificateExpiry;
    }

    public void setCertificateExpiry(Instant certificateExpiry) {
        this.certificateExpiry = certificateExpiry;
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
