package com.product.uptime.dto;

import com.product.uptime.entity.ErrorCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorUpdateDTO {
    private String url;
    private ErrorCondition errorCondition;
}