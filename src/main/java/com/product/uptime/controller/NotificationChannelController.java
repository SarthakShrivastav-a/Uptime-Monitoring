package com.product.uptime.controller;

import com.product.uptime.entity.NotificationChannel;
import com.product.uptime.repository.NotificationChannelRepository;
import com.product.uptime.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-channels")
public class NotificationChannelController {
    private final NotificationChannelRepository repository;
    private final UserService userService;

    public NotificationChannelController(NotificationChannelRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @GetMapping
    public List<NotificationChannel> list() {
        return repository.findByUserId(userService.getCurrentUserID());
    }

    @PostMapping
    public NotificationChannel create(@RequestBody NotificationChannel channel) {
        channel.setUserId(userService.getCurrentUserID());
        return repository.save(channel);
    }
}
