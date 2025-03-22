package com.product.uptime.controller;

import com.product.uptime.entity.MonitorStatus;
import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.repository.MonitorStatusRepository;
import com.product.uptime.service.MonitorService;
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
    private MonitorService monitorStatusService;

    @PostMapping("/update-status")
    public String updateMonitorStatus(@RequestBody MonitorStatusUpdate update) {
        monitorStatusService.updateMonitorStatus(update);
        return "Monitor status updated successfully!";
    }
}
