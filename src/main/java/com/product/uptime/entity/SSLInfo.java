package com.product.uptime.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
class SSLInfo {
    private String certificateIssuer;
    private Instant certificateExpiry;
    private int daysLeft;
    private String status; // VALID/EXPIRING/EXPIRED
}
