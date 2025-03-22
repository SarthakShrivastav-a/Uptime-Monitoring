package com.product.uptime.service;

import com.product.uptime.entity.*;
import com.product.uptime.repository.MonitorCheckHistoryRepository;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        monitorStatus.setStatus("UNKNOWN");
        monitorStatus.setLastChecked(Instant.now());

        MonitorStatus mon = monitorStatusRepository.save(monitorStatus);

        Monitor finalMonitor = monitor;
        executorService.submit(() -> updateMonitorSSLAndDomain(finalMonitor));
        postService.sendPostRequest(mon.getId(),monitor.getUrl(),monitor.getErrorCondition());
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
        Optional<MonitorStatus> optionalMonitorStatus = monitorStatusRepository.findById(update.getMonitorId());
        MonitorStatus monitorStatus = optionalMonitorStatus.orElse(new MonitorStatus());
        monitorStatus.setMonitorId(update.getMonitorId());
        monitorStatus.setLastChecked(update.getCheckedAt());
        if ("DOWN".equals(update.getStatus())) {
            if (!"DOWN".equals(monitorStatus.getStatus())) {
                // new downtime session log to history
                MonitorCheckHistory checkHistory = new MonitorCheckHistory(
                        update.getMonitorId(),
                        update.getStatus(),
                        update.getTriggerReason(),
                        update.getCheckedAt()
                );
                monitorCheckHistoryRepository.save(checkHistory);
                monitorStatus.setConsecutiveDowntimeCount(1);
            } else {
                monitorStatus.setConsecutiveDowntimeCount(monitorStatus.getConsecutiveDowntimeCount() + 1);
            }
        } else {
            if ("DOWN".equals(monitorStatus.getStatus())) {
                monitorStatus.setConsecutiveDowntimeCount(0);
            }
        }

        // Update the current status
        monitorStatus.setStatus(update.getStatus());

        // Update total and up checks
        monitorStatus.setTotalChecks(monitorStatus.getTotalChecks() + 1);
        if ("UP".equals(update.getStatus())) {
            monitorStatus.setUpChecks(monitorStatus.getUpChecks() + 1);
        }
        if ("DOWN".equals(update.getStatus())) {
            monitorStatus.setDownChecks(monitorStatus.getDownChecks() + 1);
            monitorStatus.setCumulativeDowntime(monitorStatus.getCumulativeDowntime() + 30);
        }

        monitorStatus.setCumulativeResponse(
                monitorStatus.getCumulativeResponse() + update.getResponseTime()
        );

        double uptimePercentage = monitorStatus.getTotalChecks() > 0 ?
                (monitorStatus.getUpChecks() * 100.0) / monitorStatus.getTotalChecks() : 0;

        double averageResponseTime = monitorStatus.getTotalChecks() > 0 ?
                monitorStatus.getCumulativeResponse() / (double) monitorStatus.getTotalChecks() : 0;


        monitorStatus.setUptimePercentage(uptimePercentage);
        monitorStatus.setAverageResponseTime(averageResponseTime);
        monitorStatusRepository.save(monitorStatus);
    }

    }