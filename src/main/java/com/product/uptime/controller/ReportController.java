package com.product.uptime.controller;

import com.product.uptime.dto.MonitorDetailsDTO;
import com.product.uptime.service.MonitorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final MonitorService monitorService;

    public ReportController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/monitors/{monitorId}.csv")
    public ResponseEntity<String> exportMonitorCsv(@PathVariable String monitorId) {
        MonitorDetailsDTO details = monitorService.getMonitorDetails(monitorId);
        String csv = "monitorId,url,currentStatus,uptimePercentage,totalChecks,upChecks,downChecks,averageResponseTime\n"
                + String.format("%s,%s,%s,%.2f,%d,%d,%d,%.2f\n",
                details.getMonitorId(),
                details.getUrl(),
                details.getCurrentStatus(),
                details.getUptimePercentage(),
                details.getTotalChecks(),
                details.getUpChecks(),
                details.getDownChecks(),
                details.getAverageResponseTime());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monitor-" + monitorId + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/monitors/{monitorId}.pdf")
    public ResponseEntity<String> exportMonitorPdfPlaceholder(@PathVariable String monitorId) {
        MonitorDetailsDTO details = monitorService.getMonitorDetails(monitorId);
        String report = "Sentinel Monitor Report\n\n"
                + "Monitor: " + details.getUrl() + "\n"
                + "Status: " + details.getCurrentStatus() + "\n"
                + "Uptime: " + details.getUptimePercentage() + "%\n"
                + "Average response time: " + details.getAverageResponseTime() + "ms\n";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monitor-" + monitorId + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(report);
    }
}
