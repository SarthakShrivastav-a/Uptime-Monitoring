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
@Document(collection = "status_pages")
public class StatusPage {
    @Id
    private String id;
    private String userId;
    private String name;
    private String slug;
    private String description;
    private boolean published = true;
    private List<StatusComponent> components = new ArrayList<>();
    private Instant createdAt = Instant.now();
}
