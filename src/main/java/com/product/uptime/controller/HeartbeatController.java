package com.product.uptime.controller;

import com.product.uptime.exception.EntityNotFoundException;
import com.product.uptime.service.CronJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/heartbeat")
public class HeartbeatController {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatController.class);

    private final CronJobService cronJobService;

    @Autowired
    public HeartbeatController(CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    @GetMapping("/{apiKey}")
    public ResponseEntity<Map<String, String>> recordHeartbeat(@PathVariable String apiKey) {
        logger.debug("Received heartbeat request for API key: {}", apiKey);

        try {
            cronJobService.recordHeartbeat(apiKey);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Heartbeat recorded"));
        } catch (EntityNotFoundException e) {
            logger.warn("Invalid API key for heartbeat: {}", apiKey);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Invalid API key"));
        } catch (Exception e) {
            logger.error("Error recording heartbeat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to record heartbeat"));
        }
    }

    // Optional: Add a POST endpoint for heartbeats with additional data
    @PostMapping("/{apiKey}")
    public ResponseEntity<Map<String, String>> recordHeartbeatWithData(
            @PathVariable String apiKey,
            @RequestBody(required = false) Map<String, Object> data) {

        logger.debug("Received POST heartbeat request for API key: {}", apiKey);

        try {
            // For now, just record the heartbeat the same as GET
            // In the future, you could extend this to save the posted data
            cronJobService.recordHeartbeat(apiKey);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Heartbeat recorded"));
        } catch (EntityNotFoundException e) {
            logger.warn("Invalid API key for heartbeat: {}", apiKey);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Invalid API key"));
        } catch (Exception e) {
            logger.error("Error recording heartbeat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to record heartbeat"));
        }
    }
}