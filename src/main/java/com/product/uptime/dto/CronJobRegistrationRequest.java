
package com.product.uptime.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronJobRegistrationRequest {
    private String name;
    private String description;
    private Long expectedIntervalSeconds;
    private Integer gracePeriodMinutes;
    private String alertEmail;
    private Boolean active;
}
