package com.product.uptime.controller;

import com.product.uptime.entity.StatusPage;
import com.product.uptime.repository.StatusPageRepository;
import com.product.uptime.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status-pages")
public class StatusPageController {
    private final StatusPageRepository statusPageRepository;
    private final UserService userService;

    public StatusPageController(StatusPageRepository statusPageRepository, UserService userService) {
        this.statusPageRepository = statusPageRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<StatusPage> list() {
        return statusPageRepository.findByUserId(userService.getCurrentUserID());
    }

    @PostMapping
    public StatusPage create(@RequestBody StatusPage statusPage) {
        statusPage.setUserId(userService.getCurrentUserID());
        return statusPageRepository.save(statusPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusPage> update(@PathVariable String id, @RequestBody StatusPage request) {
        return statusPageRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setSlug(request.getSlug());
                    existing.setDescription(request.getDescription());
                    existing.setPublished(request.isPublished());
                    existing.setComponents(request.getComponents());
                    return ResponseEntity.ok(statusPageRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
