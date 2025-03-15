package com.product.uptime.service;

import com.product.uptime.entity.*;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        monitorStatus.setUptime(0.0);
        monitorStatus.setDowntime(0);
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
    }