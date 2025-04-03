package com.product.uptime.controller;

import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
public class MonitorStatusUpdateController {


    private final MonitorService monitorService;

    @Autowired
    public MonitorStatusUpdateController(MonitorService monitorService){
        this.monitorService=monitorService;
    }

    @PostMapping("/update")
    public String updateMonitorStatus(@RequestBody MonitorStatusUpdate update) {
        System.out.println("REeached here");
        monitorService.updateMonitorStatus(update);
        return "Monitor status updated successfully!";
    }
}
