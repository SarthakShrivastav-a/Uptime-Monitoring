package com.product.uptime.dto;

import com.product.uptime.entity.ErrorCondition;
import org.springframework.data.annotation.Id;

public class MonitorDto {
    @Id
    private String url;
    private ErrorCondition errorCondition;

    public String getUrl() {
        return url;
    }

    public ErrorCondition getErrorCondition() {
        return errorCondition;
    }
}
