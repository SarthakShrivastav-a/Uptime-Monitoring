package com.product.uptime.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String authUserId;
    private String firstName;
    private String lastName;
    private String company;
    private Instant createdAt = Instant.now();
}
