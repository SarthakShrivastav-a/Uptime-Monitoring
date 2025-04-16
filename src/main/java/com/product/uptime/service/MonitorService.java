package com.product.uptime.service;

import com.product.uptime.dto.MonitorDetailsDTO;
import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.dto.MonitorUpdateDTO;
import com.product.uptime.entity.*;
import com.product.uptime.exception.EntityNotFoundException;
import com.product.uptime.repository.MonitorCheckHistoryRepository;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import com.product.uptime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MonitorService {

    @Autowired
    private MonitorRepository monitorRepository;
    @Autowired
    private  MonitorStatusRepository monitorStatusRepository;
    @Autowired
    private  SSLInfoService sslInfoService;
    @Autowired
    private  DomainInfoService domainInfoService;
    @Autowired
    private PostService postService;
    @Autowired
    private MonitorCheckHistoryRepository monitorCheckHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public Monitor createMonitor(String userId, String url, ErrorCondition errorCondition) {
        Monitor monitor = new Monitor();
        monitor.setUserId(userId);
        monitor.setUrl(url);
        monitor.setErrorCondition(errorCondition);
        monitor.setCreatedAt(Instant.now());

        monitor = monitorRepository.save(monitor);
            
        MonitorStatus monitorStatus = new MonitorStatus();
        monitorStatus.setMonitorId(monitor.getId());
        MonitorStatus mon = monitorStatusRepository.save(monitorStatus);

        Monitor finalMonitor = monitor;
        executorService.submit(() -> updateMonitorSSLAndDomain(finalMonitor));
        postService.sendPostRequest(monitor.getId(),monitor.getUrl(),monitor.getErrorCondition());
        return monitor;
        }
    private void updateMonitorSSLAndDomain(Monitor monitor) {
        try {
                SSLInfo sslInfo = sslInfoService.getSSLInfo(monitor.getUrl());
                DomainInfo domainInfo = domainInfoService.getDomainInfo(monitor.getUrl());
                monitor.setSslInfo(sslInfo);
                monitor.setDomainInfo(domainInfo);
                monitorRepository.save(monitor);
        } catch (Exception e) {
                System.err.println("Error updating SSL & Domain info for URL: " + monitor.getUrl());
                e.printStackTrace();
            }
        }


    /**
     * Updates the status of a monitor based on the provided update information.
     *
     * @param update The status update information
     * @throws EntityNotFoundException If the monitor cannot be found
     */
    public void updateMonitorStatus(MonitorStatusUpdate update) throws EntityNotFoundException {
        // Log the start of method execution
        logger.info("Starting updateMonitorStatus for monitorId: {}", update.getMonitorId());

        // Validate input
        if (update == null || update.getMonitorId() == null) {
            logger.error("Invalid update data or missing monitorId");
            throw new IllegalArgumentException("Update data cannot be null and must contain a valid monitorId");
        }

        // Find existing monitor status
        MonitorStatus monitorStatus = monitorStatusRepository.findByMonitorId(update.getMonitorId());
        logger.debug("Found existing monitorStatus: {}", monitorStatus != null ? "yes" : "no");

        // Find the monitor
        Optional<Monitor> monitorOpt = monitorRepository.findById(update.getMonitorId());
        if (!monitorOpt.isPresent()) {
            logger.error("Monitor not found for id: {}", update.getMonitorId());
            throw new EntityNotFoundException("Monitor not found with id: " + update.getMonitorId());
        }

        Monitor monitor = monitorOpt.get();
        logger.debug("Found monitor: {}", monitor.getUrl());

        // Find the user
        Optional<User> userOpt = userRepository.findById(monitor.getUserId());
        User user = null;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            logger.debug("Found user: {}", user.getEmail());
        } else {
            logger.warn("User not found for userId: {}", monitor.getUserId());
        }

        // Initialize monitor status if it doesn't exist
        if (monitorStatus == null) {
            logger.info("Creating new MonitorStatus for monitorId: {}", update.getMonitorId());
            monitorStatus = initializeNewMonitorStatus(update.getMonitorId());
        }

        // Update last checked timestamp
        monitorStatus.setLastChecked(update.getCheckedAt());

        boolean wasDown = "DOWN".equals(monitorStatus.getStatus());
        boolean isDown = "DOWN".equals(update.getStatus());

        // Handle status transitions and updates
        if (isDown) {
            handleDownStatus(monitorStatus, update, monitor, user, !wasDown);
        } else if (wasDown) {
            // Status is UP and was previously DOWN
            logger.info("Monitor {} recovered from DOWN state", update.getMonitorId());
            monitorStatus.setConsecutiveDowntimeCount(0);
        }

        // Update general metrics
        updateGeneralMetrics(monitorStatus, update);

        try {
            // Save the updated monitor status
            monitorStatusRepository.save(monitorStatus);
            logger.info("Successfully updated monitor status for monitorId: {}", update.getMonitorId());
        } catch (Exception e) {
            logger.error("Failed to save monitor status: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving monitor status: " + e.getMessage(), e);
        }
    }

    /**
     * Initialize a new monitor status object
     */
    private MonitorStatus initializeNewMonitorStatus(String monitorId) {
        MonitorStatus status = new MonitorStatus();
        status.setMonitorId(monitorId);
        status.setTotalChecks(0);
        status.setUpChecks(0);
        status.setDownChecks(0);
        status.setCumulativeDowntime(0);
        status.setCumulativeResponse(0);
        status.setConsecutiveDowntimeCount(0);
        status.setUptimePercentage(0.0);
        status.setAverageResponseTime(0.0);

        return status;
    }

    /**
     * Handle when monitor status is DOWN
     */
    private void handleDownStatus(MonitorStatus monitorStatus, MonitorStatusUpdate update,
                                  Monitor monitor, User user, boolean isNewDowntime) {
        logger.info("Processing DOWN status for monitor: {}", update.getMonitorId());

        if (isNewDowntime) {
            // If it was previously not DOWN, log and alert
            logger.warn("New downtime detected for monitor: {}", update.getMonitorId());
            recordDowntimeAndAlert(monitorStatus, update, monitor, user);
            monitorStatus.setConsecutiveDowntimeCount(1); // Start a new downtime session
        } else {
            // Continuing downtime
            int downtimeCount = (int) (monitorStatus.getConsecutiveDowntimeCount() + 1);
            monitorStatus.setConsecutiveDowntimeCount(downtimeCount);
            logger.info("Continuing downtime for monitor: {}, consecutive count: {}",
                    update.getMonitorId(), downtimeCount);
        }

        monitorStatus.setDownChecks(monitorStatus.getDownChecks() + 1);
        monitorStatus.setCumulativeDowntime(monitorStatus.getCumulativeDowntime() + 30); // Increment downtime
        monitorStatus.setStatus(update.getStatus());
    }

    /**
     * Record downtime history and send alert
     */
    private void recordDowntimeAndAlert(MonitorStatus monitorStatus, MonitorStatusUpdate update,
                                        Monitor monitor, User user) {
        // Save the check history
        try {
            MonitorCheckHistory checkHistory = new MonitorCheckHistory(
                    update.getMonitorId(),
                    update.getStatus(),
                    update.getTriggerReason(),
                    update.getCheckedAt()
            );
            monitorCheckHistoryRepository.save(checkHistory);
            logger.info("Saved monitor check history for downtime event");
        } catch (Exception e) {
            logger.error("Failed to save monitor check history", e);
        }

        // Send alert email if user exists
        if (user != null) {
            try {
                sendDowntimeAlert(monitorStatus, update, monitor, user);
            } catch (Exception e) {
                logger.error("Failed to send downtime alert email", e);
            }
        } else {
            logger.warn("Cannot send alert email - user not found for monitor: {}", update.getMonitorId());
        }
    }

    /**
     * Send downtime alert email
     */
    private void sendDowntimeAlert(MonitorStatus monitorStatus, MonitorStatusUpdate update,
                                   Monitor monitor, User user) {
        String subject = "Alert: Website Down - " + monitor.getUrl();

        String body = String.format(
                """
                Hi there,
                
                We've detected a downtime on one of your monitored endpoints.
                
                Monitor Details
                • Monitor ID       : %s
                • URL              : %s
                • Status           : %s
                • Checked At       : %s
                • Trigger Reason   : %s
                
                Current Stats
                • Total Checks     : %d
                • Uptime %%         : %.2f%%
                • Consecutive Downtime Count: %d
                • Cumulative Downtime (sec) : %d
                • Average Response Time (ms): %.2f
                
                Please investigate the issue at your earliest convenience.
                
                Regards,  
                Sentinel!
                """,
                update.getMonitorId(),
                monitor.getUrl(),
                update.getStatus(),
                update.getCheckedAt(),
                update.getTriggerReason(),
                monitorStatus.getTotalChecks(),
                monitorStatus.getUptimePercentage(),
                monitorStatus.getConsecutiveDowntimeCount(),
                monitorStatus.getCumulativeDowntime(),
                monitorStatus.getAverageResponseTime()
        );

        String recipient = user.getEmail();
        logger.info("Sending downtime alert email to: {}", recipient);
        emailService.sendEmail(recipient, body, subject);
    }

    /**
     * Update general metrics for any status
     */
    private void updateGeneralMetrics(MonitorStatus monitorStatus, MonitorStatusUpdate update) {
        monitorStatus.setStatus(update.getStatus());
        monitorStatus.setTotalChecks(monitorStatus.getTotalChecks() + 1);

        // Update response time metrics
        monitorStatus.setCumulativeResponse(monitorStatus.getCumulativeResponse() + update.getResponseTime());

        if ("UP".equals(update.getStatus())) {
            monitorStatus.setUpChecks(monitorStatus.getUpChecks() + 1);
        }

        // Calculate uptime percentage and average response time
        double uptimePercentage = monitorStatus.getTotalChecks() > 0 ?
                (monitorStatus.getUpChecks() * 100.0) / monitorStatus.getTotalChecks() : 0.0;

        double averageResponseTime = monitorStatus.getTotalChecks() > 0 ?
                (double) monitorStatus.getCumulativeResponse() / monitorStatus.getTotalChecks() : 0.0;

        monitorStatus.setUptimePercentage(uptimePercentage);
        monitorStatus.setAverageResponseTime(averageResponseTime);

        logger.debug("Updated metrics - uptime: {}%, avgResponse: {}ms, totalChecks: {}",
                uptimePercentage, averageResponseTime, monitorStatus.getTotalChecks());
    }

    public MonitorDetailsDTO getMonitorDetails(String monitorId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));
        Optional<MonitorStatus> status = Optional.ofNullable(monitorStatusRepository.findByMonitorId(monitorId));
        List<MonitorCheckHistory> checkHistory = monitorCheckHistoryRepository.findByMonitorId(monitorId);

        MonitorDetailsDTO dto = new MonitorDetailsDTO();
        dto.setMonitorId(monitor.getId());
        dto.setUserId(monitor.getUserId());
        dto.setUrl(monitor.getUrl());
        dto.setErrorCondition(monitor.getErrorCondition());
        dto.setSslInfo(monitor.getSslInfo());
        dto.setDomainInfo(monitor.getDomainInfo());
        dto.setCreatedAt(monitor.getCreatedAt());

        // Ensure the correct MonitorStatus is fetched
        if (status.isPresent()) {
            MonitorStatus monitorStatus = status.get();
            dto.setCurrentStatus(monitorStatus.getStatus());
            dto.setUptimePercentage(monitorStatus.getUptimePercentage());
            dto.setTotalChecks(monitorStatus.getTotalChecks());
            dto.setDownChecks(monitorStatus.getDownChecks());
            dto.setCumulativeDowntime(monitorStatus.getCumulativeDowntime());
            dto.setConsecutiveDowntimeCount(monitorStatus.getConsecutiveDowntimeCount());
            dto.setCumulativeResponse(monitorStatus.getCumulativeResponse());
            dto.setAverageResponseTime(monitorStatus.getAverageResponseTime());
            dto.setLastChecked(monitorStatus.getLastChecked());
            dto.setUpChecks(monitorStatus.getUpChecks());
        }

        dto.setCheckHistory(checkHistory);
        return dto;
    }

    public void deleteMonitor(String id) throws EntityNotFoundException {
        Optional<Monitor> monitorOptional = monitorRepository.findById(id);
        if (monitorOptional.isPresent()) {
            Monitor monitor = monitorOptional.get();
            monitorStatusRepository.deleteByMonitorId(id);
            monitorRepository.deleteById(id);
            postService.sendDeleteRequest(id);

            logger.info("Monitor with ID {} has been deleted", id);
        } else {
            logger.warn("Attempted to delete non-existent monitor with ID: {}", id);
            throw new EntityNotFoundException("Monitor with ID " + id + " not found");
        }
    }
    public Monitor updateMonitor(String id, MonitorUpdateDTO updateDTO) throws EntityNotFoundException {
        Optional<Monitor> existingMonitorOptional = monitorRepository.findById(id);

        if (existingMonitorOptional.isEmpty()) {
            throw new EntityNotFoundException("Monitor with ID " + id + " not found");
        }

        Monitor existingMonitor = existingMonitorOptional.get();

        if (updateDTO.getUrl() != null) {
            existingMonitor.setUrl(updateDTO.getUrl());
        }

        if (updateDTO.getErrorCondition() != null) {
            existingMonitor.setErrorCondition(updateDTO.getErrorCondition());

            postService.sendPostRequest(id, existingMonitor.getUrl(), existingMonitor.getErrorCondition());
        }

        // Save the updated monitor
        Monitor savedMonitor = monitorRepository.save(existingMonitor);

        // If URL changed, we might want to update SSL and domain info
        if (updateDTO.getUrl() != null && !updateDTO.getUrl().equals(existingMonitor.getUrl())) {
            executorService.submit(() -> updateMonitorSSLAndDomain(savedMonitor));
        }

        return savedMonitor;
    }
}
