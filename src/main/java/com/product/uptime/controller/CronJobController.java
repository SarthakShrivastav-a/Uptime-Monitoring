package com.product.uptime.controller;

import com.product.uptime.dto.CronJobDetailsDTO;
import com.product.uptime.dto.CronJobRegistrationRequest;
import com.product.uptime.entity.CronJob;
import com.product.uptime.entity.HeartbeatLog;
import com.product.uptime.entity.User;
import com.product.uptime.exception.EntityNotFoundException;
import com.product.uptime.repository.UserRepository;
import com.product.uptime.service.CronJobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class CronJobController {

    private static final Logger logger = LoggerFactory.getLogger(CronJobController.class);

    private final CronJobService cronJobService;
    private final UserRepository userRepository;

    @Autowired
    public CronJobController(CronJobService cronJobService, UserRepository userRepository) {
        this.cronJobService = cronJobService;
        this.userRepository = userRepository;
    }

    // Utility method to extract current user ID from security context
    private String getCurrentUserID() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found: " + email));
        return user.getId();
    }

    // List all jobs for authenticated user
    @GetMapping
    public ResponseEntity<List<CronJob>> getUserJobs() {
        String userId = getCurrentUserID();
        logger.debug("Listing jobs for user: {}", userId);
        List<CronJob> jobs = cronJobService.getUserJobs(userId);
        return ResponseEntity.ok(jobs);
    }

    // Get a specific job by ID
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJob(@PathVariable String jobId) {
        String userId = getCurrentUserID();
        logger.debug("Fetching job {} for user: {}", jobId, userId);

        try {
            CronJobDetailsDTO job = cronJobService.getJobDetails(userId, jobId);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            logger.warn("Job not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching job: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to fetch job details"));
        }
    }

    // Create a new job (CREATE)
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody CronJobRegistrationRequest request) {
        String userId = getCurrentUserID();
        logger.debug("Creating new job for user: {}", userId);

        try {
            CronJob newJob = cronJobService.registerJob(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to create job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid job creation request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating job: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to create job"));
        }
    }

    // Update an existing job (UPDATE)
    @PutMapping("/{jobId}")
    public ResponseEntity<?> updateJob(
            @PathVariable String jobId,
            @RequestBody CronJobRegistrationRequest request) {

        String userId = getCurrentUserID();
        logger.debug("Updating job {} for user: {}", jobId, userId);

        try {
            cronJobService.updateJob(userId, jobId, request);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Job updated successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to update job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid job update request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating job: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to update job"));
        }
    }

    // Delete a job (DELETE)
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable String jobId) {
        String userId = getCurrentUserID();
        logger.debug("Deleting job {} for user: {}", jobId, userId);

        try {
            cronJobService.deleteJob(userId, jobId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Job deleted successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to delete job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting job: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to delete job"));
        }
    }

    // Get logs for a specific job
    @GetMapping("/{jobId}/logs")
    public ResponseEntity<?> getJobLogs(@PathVariable String jobId) {
        String userId = getCurrentUserID();
        logger.debug("Fetching logs for job {} (user: {})", jobId, userId);

        try {
            List<HeartbeatLog> logs = cronJobService.getUserJobLogs(userId, jobId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Error fetching job logs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to fetch job logs"));
        }
    }

    // Regenerate API key for a job
    @PostMapping("/{jobId}/regenerate-key")
    public ResponseEntity<?> regenerateApiKey(@PathVariable String jobId) {
        String userId = getCurrentUserID();
        logger.debug("Regenerating API key for job {} (user: {})", jobId, userId);

        try {
            cronJobService.regenerateApiKey(userId, jobId);
            CronJobDetailsDTO job = cronJobService.getJobDetails(userId, jobId);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to regenerate API key: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error regenerating API key: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to regenerate API key"));
        }
    }
}