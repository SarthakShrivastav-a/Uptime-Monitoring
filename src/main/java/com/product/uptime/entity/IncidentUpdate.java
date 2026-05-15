package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentUpdate {
    private String state;
    private String message;
    private Instant createdAt = Instant.now();
}
