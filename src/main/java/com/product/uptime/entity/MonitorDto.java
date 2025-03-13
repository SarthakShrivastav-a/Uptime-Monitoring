package com.product.uptime.entity;

import org.springframework.data.annotation.Id;

public class MonitorDto {
    @Id
    private String id;
    private String url;
    private ErrorCondition errorCondition;
}
