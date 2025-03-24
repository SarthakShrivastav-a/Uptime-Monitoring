package com.product.uptime.controller;

import com.product.uptime.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
public class MonitorStatusUpdate {

    private final MonitorService monitorService;

    @Autowired
    public MonitorStatusUpdate(MonitorService monitorService){
        this.monitorService=monitorService;
    }

    @PostMapping("/update")
    public String updateMonitorStatus(@RequestBody com.product.uptime.dto.MonitorStatusUpdate update) {
        monitorService.updateMonitorStatus(update);
        return "Monitor status updated successfully!";
    }
}
