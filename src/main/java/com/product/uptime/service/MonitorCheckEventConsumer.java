package com.product.uptime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.events.MonitorCheckCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MonitorCheckEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MonitorCheckEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final MonitorService monitorService;

    public MonitorCheckEventConsumer(ObjectMapper objectMapper, MonitorService monitorService) {
        this.objectMapper = objectMapper;
        this.monitorService = monitorService;
    }

    @KafkaListener(topics = "${sentinel.kafka.monitor-checks-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) {
        try {
            MonitorCheckCompletedEvent event = objectMapper.readValue(payload, MonitorCheckCompletedEvent.class);
            MonitorStatusUpdate update = new MonitorStatusUpdate(
                    event.getMonitorId(),
                    event.getStatus(),
                    (int) event.getResponseTime(),
                    event.getTriggerReason(),
                    event.getCheckedAt()
            );
            monitorService.updateMonitorStatus(update);
            logger.info("Processed monitor check event {} for monitor {}", event.getEventId(), event.getMonitorId());
        } catch (Throwable e) {
            logger.error("Failed to process monitor check event: {}", e.getMessage(), e);
        }
    }
}
