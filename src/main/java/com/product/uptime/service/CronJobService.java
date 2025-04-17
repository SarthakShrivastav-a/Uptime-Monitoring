package com.product.uptime.service;

import com.product.uptime.entity.CronJob;
import com.product.uptime.entity.HeartbeatLog;
import com.product.uptime.entity.User;
import com.product.uptime.dto.CronJobRegistrationRequest;
import com.product.uptime.dto.CronJobDetailsDTO;
import com.product.uptime.exception.EntityNotFoundException;
import com.product.uptime.repository.CronJobRepository;
import com.product.uptime.repository.HeartbeatLogRepository;
import com.product.uptime.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Service
public class CronJobService {

    private static final Logger logger = LoggerFactory.getLogger(CronJobService.class);

    private final CronJobRepository cronJobRepository;
    private final HeartbeatLogRepository heartbeatLogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public CronJobService(CronJobRepository cronJobRepository,
                          HeartbeatLogRepository heartbeatLogRepository,
                          UserRepository userRepository,
                          EmailService emailService) {
        this.cronJobRepository = cronJobRepository;
        this.heartbeatLogRepository = heartbeatLogRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<CronJob> getAllJobs() {
        return cronJobRepository.findAll();
    }

    public List<CronJob> getUserJobs(String userId) {
        logger.info("Fetching jobs for user: {}", userId);
        return cronJobRepository.findByUserId(userId);
    }

    public Optional<CronJob> getJobById(String id) {
        return cronJobRepository.findById(id);
    }

    public Optional<CronJob> getUserJobById(String userId, String jobId) {
        logger.info("Fetching job {} for user: {}", jobId, userId);
        return cronJobRepository.findByUserIdAndId(userId, jobId);
    }

    public CronJob registerJob(String userId, CronJobRegistrationRequest request) throws EntityNotFoundException {
        logger.info("Creating new job for user: {}", userId);

        // Check if job with this name already exists for this user
        if (cronJobRepository.findByUserIdAndName(userId, request.getName()).isPresent()) {
            logger.warn("Job with name '{}' already exists for user {}", request.getName(), userId);
            throw new IllegalArgumentException("Job with this name already exists for this user");
        }

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        CronJob job = new CronJob();
        job.setUserId(userId);
        job.setName(request.getName());
        job.setDescription(request.getDescription());
        job.setExpectedIntervalSeconds(request.getExpectedIntervalSeconds());
        job.setGracePeriodMinutes(request.getGracePeriodMinutes() != null ? request.getGracePeriodMinutes() : 5);
        job.setAlertEmail(request.getAlertEmail());
        job.setActive(request.getActive() != null ? request.getActive() : true);

        // Generate a unique API key
        job.setApiKey(UUID.randomUUID().toString());

        logger.info("Saving new cronjob with name: {}", request.getName());
        return cronJobRepository.save(job);
    }

    public void updateJob(String userId, String jobId, CronJobRegistrationRequest request) throws EntityNotFoundException {
        logger.info("Updating job {} for user: {}", jobId, userId);

        CronJob job = cronJobRepository.findByUserIdAndId(userId, jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found or you don't have permission"));

        // Check if updating to a name that already exists for this user
        if (request.getName() != null && !request.getName().equals(job.getName())) {
            if (cronJobRepository.findByUserIdAndName(userId, request.getName()).isPresent()) {
                logger.warn("Cannot update job - name '{}' already exists for user {}", request.getName(), userId);
                throw new IllegalArgumentException("Job with this name already exists for this user");
            }
            job.setName(request.getName());
        }

        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getExpectedIntervalSeconds() != null) job.setExpectedIntervalSeconds(request.getExpectedIntervalSeconds());
        if (request.getGracePeriodMinutes() != null) job.setGracePeriodMinutes(request.getGracePeriodMinutes());
        if (request.getAlertEmail() != null) job.setAlertEmail(request.getAlertEmail());
        if (request.getActive() != null) job.setActive(request.getActive());

        logger.info("Saving updated job: {}", job.getId());
        cronJobRepository.save(job);
    }

    public void recordHeartbeat(String apiKey) throws EntityNotFoundException {
        logger.debug("Recording heartbeat for API key: {}", apiKey);

        CronJob job = cronJobRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new EntityNotFoundException("Invalid API key"));

        LocalDateTime now = LocalDateTime.now();
        job.setLastHeartbeat(now);
        job.setNextExpectedHeartbeat(now.plusSeconds(job.getExpectedIntervalSeconds()));

        // Log the heartbeat
        HeartbeatLog log = new HeartbeatLog();
        log.setCronJobId(job.getId());
        log.setUserId(job.getUserId());
        log.setCronJobName(job.getName());
        log.setTimestamp(now);
        log.setStatus("SUCCESS");

        cronJobRepository.save(job);
        heartbeatLogRepository.save(log);

        logger.debug("Heartbeat recorded successfully for job: {}", job.getId());
    }

    public CronJobDetailsDTO getJobDetails(String userId, String jobId) throws EntityNotFoundException {
        logger.info("Fetching job details for job {} (user: {})", jobId, userId);

        CronJob job = cronJobRepository.findByUserIdAndId(userId, jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found or you don't have permission"));

        List<HeartbeatLog> logs = heartbeatLogRepository.findByUserIdAndCronJobIdOrderByTimestampDesc(userId, jobId);

        CronJobDetailsDTO details = new CronJobDetailsDTO();
        details.setId(job.getId());
        details.setUserId(job.getUserId());
        details.setName(job.getName());
        details.setDescription(job.getDescription());
        details.setExpectedIntervalSeconds(job.getExpectedIntervalSeconds());
        details.setLastHeartbeat(job.getLastHeartbeat());
        details.setNextExpectedHeartbeat(job.getNextExpectedHeartbeat());
        details.setGracePeriodMinutes(job.getGracePeriodMinutes());
        details.setActive(job.getActive());
        details.setAlertEmail(job.getAlertEmail());
        details.setApiKey(job.getApiKey());

        // Calculate stats
        long totalChecks = logs.size();
        details.setTotalChecks(totalChecks);

        long successChecks = logs.stream().filter(log -> "SUCCESS".equals(log.getStatus())).count();
        details.setSuccessChecks(successChecks);
        details.setMissedChecks(totalChecks - successChecks);

        // Calculate uptime percentage if there are logs
        if (totalChecks > 0) {
            details.setUptimePercentage((double) successChecks / totalChecks * 100.0);
        } else {
            details.setUptimePercentage(0.0);
        }

        // Determine current status
        if (job.getLastHeartbeat() != null) {
            LocalDateTime expectedNextHeartbeat = job.getLastHeartbeat().plusSeconds(job.getExpectedIntervalSeconds());
            LocalDateTime graceDeadline = expectedNextHeartbeat.plusMinutes(job.getGracePeriodMinutes());

            if (LocalDateTime.now().isAfter(graceDeadline)) {
                details.setCurrentStatus("DOWN");
            } else {
                details.setCurrentStatus("UP");
            }
        } else {
            details.setCurrentStatus("UNKNOWN");
        }

        details.setHeartbeatHistory(logs);

        return details;
    }

    public List<HeartbeatLog> getJobLogs(String jobId) {
        return heartbeatLogRepository.findByCronJobIdOrderByTimestampDesc(jobId);
    }

    public List<HeartbeatLog> getUserJobLogs(String userId, String jobId) {
        return heartbeatLogRepository.findByUserIdAndCronJobIdOrderByTimestampDesc(userId, jobId);
    }

    public List<HeartbeatLog> getAllUserLogs(String userId) {
        return heartbeatLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public void deleteJob(String userId, String jobId) throws EntityNotFoundException {
        logger.info("Deleting job {} for user: {}", jobId, userId);

        CronJob job = cronJobRepository.findByUserIdAndId(userId, jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found or you don't have permission"));

        cronJobRepository.delete(job);
        heartbeatLogRepository.deleteByCronJobId(jobId);

        logger.info("Job deleted successfully: {}", jobId);
    }

    public void regenerateApiKey(String userId, String jobId) throws EntityNotFoundException {
        logger.info("Regenerating API key for job {} (user: {})", jobId, userId);

        CronJob job = cronJobRepository.findByUserIdAndId(userId, jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found or you don't have permission"));

        job.setApiKey(UUID.randomUUID().toString());
        cronJobRepository.save(job);

        logger.info("API key regenerated for job: {}", jobId);
    }
}