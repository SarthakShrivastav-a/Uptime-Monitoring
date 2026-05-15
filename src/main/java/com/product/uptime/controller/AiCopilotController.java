package com.product.uptime.controller;

import com.product.uptime.repository.IncidentRepository;
import com.product.uptime.service.AiCopilotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/copilot")
public class AiCopilotController {
    private final IncidentRepository incidentRepository;
    private final AiCopilotService aiCopilotService;

    public AiCopilotController(IncidentRepository incidentRepository, AiCopilotService aiCopilotService) {
        this.incidentRepository = incidentRepository;
        this.aiCopilotService = aiCopilotService;
    }

    @PostMapping("/incidents/{incidentId}/summary")
    public ResponseEntity<Map<String, Object>> summarizeIncident(@PathVariable String incidentId) {
        return incidentRepository.findById(incidentId)
                .map(incident -> ResponseEntity.ok(aiCopilotService.summarizeIncident(incident)))
                .orElse(ResponseEntity.notFound().build());
    }
}
