package com.product.uptime.service;

import com.product.uptime.entity.*;
import com.product.uptime.repository.MonitorRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitorService {

    @Service
    @RequiredArgsConstructor
    public class MonitorService {

        private final MonitorRepository monitorRepository;
        private final MonitorStatusRepository monitorStatusRepository;
        private final SSLInfoService sslInfoService;
        private final DomainInfoService domainInfoService;
        private final ExecutorService executorService = Executors.newFixedThreadPool(5); // For async execution

        public Monitor createMonitor(String userId, String url, ErrorCondition errorCondition) {
            // 1️⃣ Create & Save Monitor Entry
            Monitor monitor = new Monitor();
            monitor.setUserId(userId);
            monitor.setUrl(url);
            monitor.setErrorCondition(errorCondition);
            monitor.setCreatedAt(Instant.now());

            monitor = monitorRepository.save(monitor);

            // 2️⃣ Create & Save Initial MonitorStatus Entry
            MonitorStatus monitorStatus = new MonitorStatus();
            monitorStatus.setMonitorId(monitor.getId());
            monitorStatus.setStatus("UNKNOWN"); // Default state before monitoring starts
            monitorStatus.setUptime(0.0);
            monitorStatus.setDowntime(0);
            monitorStatus.setLastChecked(Instant.now());

            monitorStatusRepository.save(monitorStatus);

            // 3️⃣ Fetch SSL & Domain Info Asynchronously
            Monitor finalMonitor = monitor;
            executorService.submit(() -> updateMonitorSSLAndDomain(finalMonitor));

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

}
