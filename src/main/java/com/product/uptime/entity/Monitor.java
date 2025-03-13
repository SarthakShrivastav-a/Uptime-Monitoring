package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "monitors")
public class Monitor {
    @Id
    private String id;
    private String userId;
    private String url;

    private ErrorCondition errorCondition;
    private String status;
    private double uptime;
    private int downtime;
    private Instant lastChecked;

    private SSLInfo sslInfo;
    private DomainInfo domainInfo;
    private Instant createdAt = Instant.now();
}
