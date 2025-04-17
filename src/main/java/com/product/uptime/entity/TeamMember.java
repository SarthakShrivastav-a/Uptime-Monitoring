package com.product.uptime.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class TeamMember {

    private String firstName;
    private String lastName;
    private String email;
    private boolean active;

}
