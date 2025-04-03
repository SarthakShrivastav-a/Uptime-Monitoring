package com.product.uptime.service;

import com.product.uptime.dto.MonitorDetailsDTO;
import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.entity.*;
import com.product.uptime.repository.MonitorCheckHistoryRepository;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    public void updateMonitorStatus(MonitorStatusUpdate update) {
        MonitorStatus monitorStatus = monitorStatusRepository.findByMonitorId(update.getMonitorId());

        // If monitor status does not exist, create a new one
        if (monitorStatus == null) {
            monitorStatus = new MonitorStatus();
            monitorStatus.setMonitorId(update.getMonitorId());
            monitorStatus.setTotalChecks(0);
            monitorStatus.setUpChecks(0);
            monitorStatus.setDownChecks(0);
            monitorStatus.setCumulativeDowntime(0);
            monitorStatus.setCumulativeResponse(0);
            monitorStatus.setConsecutiveDowntimeCount(0);
            monitorStatus.setUptimePercentage(0.0);
            monitorStatus.setAverageResponseTime(0.0);
        }

        monitorStatus.setLastChecked(update.getCheckedAt());

        // If status is DOWN
        if ("DOWN".equals(update.getStatus())) {
            // If it was previously UP, log a new downtime session
            if (!"DOWN".equals(monitorStatus.getStatus())) {
                MonitorCheckHistory checkHistory = new MonitorCheckHistory(update.getMonitorId(),update.getStatus(),update.getTriggerReason(),update.getCheckedAt());
                monitorCheckHistoryRepository.save(checkHistory);

                monitorStatus.setConsecutiveDowntimeCount(1); // Start a new downtime session
            } else {
                monitorStatus.setConsecutiveDowntimeCount(monitorStatus.getConsecutiveDowntimeCount() + 1);
            }

            monitorStatus.setDownChecks(monitorStatus.getDownChecks() + 1);
            monitorStatus.setCumulativeDowntime(monitorStatus.getCumulativeDowntime() + 30); // Increment downtime
        }

        // If status is UP and was previously DOWN, reset consecutive downtime
        if ("UP".equals(update.getStatus()) && "DOWN".equals(monitorStatus.getStatus())) {
            monitorStatus.setConsecutiveDowntimeCount(0);
        }

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

        // Save the updated monitor status
        monitorStatusRepository.save(monitorStatus);
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

}
