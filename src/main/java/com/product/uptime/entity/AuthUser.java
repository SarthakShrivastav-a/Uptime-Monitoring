package com.product.uptime.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auth_users")
public class AuthUser {
    @Id
    private String id;
    private String email;
    private String password;
    private List<String> roles;
    private Instant createdAt = Instant.now();
}
