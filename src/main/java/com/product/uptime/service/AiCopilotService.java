package com.product.uptime.service;

import com.product.uptime.entity.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiCopilotService {
    private static final Logger logger = LoggerFactory.getLogger(AiCopilotService.class);

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AiCopilotService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> summarizeIncident(Incident incident) {
        Map<String, Object> request = Map.of(
                "incident_id", incident.getId(),
                "title", incident.getTitle(),
                "state", incident.getState(),
                "severity", incident.getSeverity(),
                "updates", incident.getUpdates()
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/incident-summary",
                    request,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            logger.warn("AI service unavailable, using fallback summary: {}", e.getMessage());
            return Map.of(
                    "summary", "Incident " + incident.getTitle() + " is currently " + incident.getState(),
                    "likely_cause", "Review monitor status, recent deployments, and infrastructure health.",
                    "recommended_actions", new String[]{"Check service logs", "Verify network/DNS", "Confirm recovery from dashboard"},
                    "fallback", true
            );
        }
    }
}
