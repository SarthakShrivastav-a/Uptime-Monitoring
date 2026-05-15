package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusComponent {
    private String name;
    private String type;
    private String linkedResourceId;
    private String state = "OPERATIONAL";
}
