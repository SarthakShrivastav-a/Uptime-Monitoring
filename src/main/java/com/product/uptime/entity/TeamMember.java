package com.product.uptime.entity;

import lombok.Data;

@Data
public class TeamMember {

    private String firstName;
    private String lastName;
    private String email;
    private boolean active;

}
