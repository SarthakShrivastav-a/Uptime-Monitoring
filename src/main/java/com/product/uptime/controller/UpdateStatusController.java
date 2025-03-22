package com.product.uptime.controller;

import com.product.uptime.entity.MonitorStatus;
import com.product.uptime.entity.MonitorStatusUpdate;
import com.product.uptime.repository.MonitorStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/private")
public class UpdateStatusController {

    @Autowired
    MonitorStatusRepository monitorStatusRepository;
    @PostMapping("/update")
    public String updateMonitorStatus(@RequestBody MonitorStatusUpdate request) {
        Optional<MonitorStatus> monitorOptional = monitorStatusRepository.findById(request.getMonitorId());

        if (monitorOptional.isPresent()) {
            MonitorStatus monitor = monitorOptional.get();
            monitor.setStatus("DOWN");
            monitor.setLastChecked(request.getCheckedAt());
            monitorStatusRepository.save(monitor);
            return "Monitor status updated successfully!";
        } else {
            return "Monitor not found!";
        }
    }
}
