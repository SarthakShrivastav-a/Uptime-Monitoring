package com.product.uptime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.uptime.events.MonitorLifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MonitorEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(MonitorEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sentinel.kafka.monitor-lifecycle-topic}")
    private String monitorLifecycleTopic;

    public MonitorEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(MonitorLifecycleEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(monitorLifecycleTopic, event.getMonitorId(), payload);
            logger.info("Published monitor lifecycle event {} for monitor {}", event.getEventType(), event.getMonitorId());
        } catch (Exception e) {
            logger.warn("Failed to publish monitor lifecycle event for monitor {}: {}", event.getMonitorId(), e.getMessage());
        }
    }
}
