package com.product.uptime.service;
import com.product.uptime.entity.CronJob;
import com.product.uptime.entity.HeartbeatLog;
import com.product.uptime.entity.User;
import com.product.uptime.repository.CronJobRepository;
import com.product.uptime.repository.HeartbeatLogRepository;
import com.product.uptime.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HeartbeatMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatMonitoringService.class);

    private final CronJobRepository cronJobRepository;
    private final HeartbeatLogRepository heartbeatLogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public HeartbeatMonitoringService(CronJobRepository cronJobRepository,
                             HeartbeatLogRepository heartbeatLogRepository,
                             UserRepository userRepository,
                             EmailService emailService) {
        this.cronJobRepository = cronJobRepository;
        this.heartbeatLogRepository = heartbeatLogRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkMissedHeartbeats() {
        logger.debug("Starting scheduled check for missed heartbeats");

        LocalDateTime now = LocalDateTime.now();
        List<CronJob> activeCronJobs = cronJobRepository.findByActiveTrue();

        logger.debug("Checking {} active jobs for missed heartbeats", activeCronJobs.size());

        for (CronJob job : activeCronJobs) {
            // Skip if job has no heartbeats yet
            if (job.getNextExpectedHeartbeat() == null) {
                continue;
            }

            // Calculate grace period deadline
            LocalDateTime deadline = job.getNextExpectedHeartbeat()
                    .plusMinutes(job.getGracePeriodMinutes());

            // Check if heartbeat is missed beyond grace period
            if (now.isAfter(deadline)) {
                logger.info("Missed heartbeat detected for job: {}", job.getId());

                // Log the missed heartbeat
                HeartbeatLog log = new HeartbeatLog();
                log.setCronJobId(job.getId());
                log.setUserId(job.getUserId());
                log.setCronJobName(job.getName());
                log.setTimestamp(now);
                log.setStatus("MISSED");
                heartbeatLogRepository.save(log);

                // Send alert
                sendAlert(job);
            }
        }
    }

    private void sendAlert(CronJob job) {
        logger.info("Sending alert for missed heartbeat on job: {}", job.getId());

        // Find the job owner
        Optional<User> userOpt = userRepository.findById(job.getUserId());
        if (userOpt.isEmpty()) {
            logger.warn("Could not find user {} for job {}, can't send alert", job.getUserId(), job.getId());
            return;
        }

        User user = userOpt.get();

        // Determine email recipient - use job's alertEmail if set, otherwise use user's email
        String recipient = job.getAlertEmail() != null && !job.getAlertEmail().isEmpty()
                ? job.getAlertEmail()
                : user.getEmail();

        // Build email content
        String subject = "Alert: Missed Heartbeat - " + job.getName();

        String body = String.format(
                """
                Hi %s,
                
                We've detected a missed heartbeat for your cronjob:
                
                Job Details:
                • Name: %s
                • Description: %s
                • Last Heartbeat: %s
                • Expected Next Heartbeat: %s
                • Grace Period: %d minutes
                
                This could indicate that your scheduled task has failed to run.
                Please check your system to ensure everything is working properly.
                
                You can view more details in your dashboard.
                
                Regards,
                Your Monitoring Service
                """,
                user.getFirstName() != null ? user.getFirstName() : "there",
                job.getName(),
                job.getDescription() != null ? job.getDescription() : "No description",
                job.getLastHeartbeat() != null ? job.getLastHeartbeat().toString() : "Never",
                job.getNextExpectedHeartbeat() != null ? job.getNextExpectedHeartbeat().toString() : "N/A",
                job.getGracePeriodMinutes()
        );

        // Send the email
        try {
            emailService.sendEmail(recipient, body, subject);
            logger.info("Alert email sent to {} for job {}", recipient, job.getId());
        } catch (Exception e) {
            logger.error("Failed to send alert email: {}", e.getMessage(), e);
        }
    }
}