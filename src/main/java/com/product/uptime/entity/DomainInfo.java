package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
class DomainInfo {
    private Instant domainExpiry;
    private int daysLeft;
    private String status; // ACTIVE/EXPIRING/EXPIRED
}