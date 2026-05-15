package com.product.uptime.controller;

import com.product.uptime.entity.Incident;
import com.product.uptime.entity.IncidentUpdate;
import com.product.uptime.repository.IncidentRepository;
import com.product.uptime.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentRepository incidentRepository;
    private final UserService userService;

    public IncidentController(IncidentRepository incidentRepository, UserService userService) {
        this.incidentRepository = incidentRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<Incident> list() {
        return incidentRepository.findByUserIdOrderByCreatedAtDesc(userService.getCurrentUserID());
    }

    @PostMapping
    public Incident create(@RequestBody Incident incident) {
        incident.setUserId(userService.getCurrentUserID());
        if (incident.getUpdates().isEmpty()) {
            incident.getUpdates().add(new IncidentUpdate(incident.getState(), "Incident created", Instant.now()));
        }
        return incidentRepository.save(incident);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Incident> updateState(@PathVariable String id, @RequestBody Map<String, String> request) {
        return incidentRepository.findById(id)
                .map(incident -> {
                    String state = request.getOrDefault("state", incident.getState());
                    incident.setState(state);
                    incident.getUpdates().add(new IncidentUpdate(state, request.getOrDefault("message", "State updated"), Instant.now()));
                    if ("RESOLVED".equals(state)) {
                        incident.setResolvedAt(Instant.now());
                    }
                    return ResponseEntity.ok(incidentRepository.save(incident));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/updates")
    public ResponseEntity<Incident> addUpdate(@PathVariable String id, @RequestBody IncidentUpdate update) {
        return incidentRepository.findById(id)
                .map(incident -> {
                    incident.getUpdates().add(update);
                    return ResponseEntity.ok(incidentRepository.save(incident));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
