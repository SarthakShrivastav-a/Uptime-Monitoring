package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents")
public class Incident {
    @Id
    private String id;
    private String userId;
    private String title;
    private String severity = "MEDIUM";
    private String state = "INVESTIGATING";
    private List<String> affectedComponentIds = new ArrayList<>();
    private String assignedToEmail;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private List<IncidentUpdate> updates = new ArrayList<>();
    private Instant createdAt = Instant.now();
}
