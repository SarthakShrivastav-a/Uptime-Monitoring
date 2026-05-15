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
@Document(collection = "maintenance_windows")
public class MaintenanceWindow {
    @Id
    private String id;
    private String userId;
    private String title;
    private String description;
    private Instant startsAt;
    private Instant endsAt;
    private List<String> affectedResourceIds = new ArrayList<>();
    private Instant createdAt = Instant.now();
}
