package com.product.uptime.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cronjobs")
@CompoundIndexes({
        @CompoundIndex(name = "userId_name_idx", def = "{'userId': 1, 'name': 1}", unique = true)
})
public class CronJob {
    @Id
    private String id;

    private String userId;

    private String name;
    private String description;
    private Long expectedIntervalSeconds;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime nextExpectedHeartbeat;
    private Integer gracePeriodMinutes;
    private Boolean active = true;
    private String alertEmail;

    @Indexed(unique = true)
    private String apiKey;

    private Instant createdAt = Instant.now();
}