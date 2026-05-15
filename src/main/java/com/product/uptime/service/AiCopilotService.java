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
        Map<String, Object> request = incidentPayload(incident);
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

    public Map<String, Object> rootCauseHints(Incident incident) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/root-cause-hints",
                    incidentPayload(incident),
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            logger.warn("AI service unavailable, using fallback root cause hints: {}", e.getMessage());
            return Map.of(
                    "hints", new String[]{"Check recent monitor failures", "Compare status codes and response times", "Review deployment and DNS changes"},
                    "confidence", "low",
                    "fallback", true
            );
        }
    }

    public Map<String, Object> draftPostmortem(Incident incident) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/postmortem-draft",
                    incidentPayload(incident),
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            logger.warn("AI service unavailable, using fallback postmortem draft: {}", e.getMessage());
            return Map.of(
                    "executive_summary", "Incident " + incident.getTitle() + " required investigation and follow-up.",
                    "impact", "User impact should be confirmed from monitor history and support reports.",
                    "timeline", new String[]{"Incident detected", "Investigation started", "Resolution pending or recorded"},
                    "root_cause_hypothesis", "Review monitor status, infrastructure health, and recent changes.",
                    "resolution", "Document the exact remediation once confirmed.",
                    "prevention_tasks", new String[]{"Add better alert context", "Review runbook coverage", "Confirm owner escalation paths"},
                    "fallback", true
            );
        }
    }

    private Map<String, Object> incidentPayload(Incident incident) {
        return Map.of(
                "incident_id", incident.getId(),
                "title", incident.getTitle(),
                "state", incident.getState(),
                "severity", incident.getSeverity(),
                "updates", incident.getUpdates()
        );
    }
}
