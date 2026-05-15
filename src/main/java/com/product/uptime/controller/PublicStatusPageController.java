package com.product.uptime.controller;

import com.product.uptime.entity.StatusPage;
import com.product.uptime.repository.StatusPageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/status-pages")
public class PublicStatusPageController {
    private final StatusPageRepository statusPageRepository;

    public PublicStatusPageController(StatusPageRepository statusPageRepository) {
        this.statusPageRepository = statusPageRepository;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<StatusPage> getPublicStatusPage(@PathVariable String slug) {
        return statusPageRepository.findBySlug(slug)
                .filter(StatusPage::isPublished)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
