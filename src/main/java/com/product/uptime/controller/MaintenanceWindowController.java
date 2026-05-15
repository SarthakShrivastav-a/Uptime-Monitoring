package com.product.uptime.controller;

import com.product.uptime.entity.MaintenanceWindow;
import com.product.uptime.repository.MaintenanceWindowRepository;
import com.product.uptime.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-windows")
public class MaintenanceWindowController {
    private final MaintenanceWindowRepository repository;
    private final UserService userService;

    public MaintenanceWindowController(MaintenanceWindowRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @GetMapping
    public List<MaintenanceWindow> list() {
        return repository.findByUserIdOrderByStartsAtDesc(userService.getCurrentUserID());
    }

    @PostMapping
    public MaintenanceWindow create(@RequestBody MaintenanceWindow window) {
        window.setUserId(userService.getCurrentUserID());
        return repository.save(window);
    }
}
