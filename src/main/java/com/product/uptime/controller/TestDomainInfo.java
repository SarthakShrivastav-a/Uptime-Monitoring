package com.product.uptime.controller;

import com.product.uptime.service.DomainInfoService;
import com.product.uptime.entity.DomainInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestDomainInfo {

    private final DomainInfoService domainInfoService;

    public TestDomainInfo(DomainInfoService domainInfoService) {
        this.domainInfoService = domainInfoService;
    }

    @GetMapping("/domain-info")
    public ResponseEntity<?> getDomainInfo() {
        String testUrl = "https://google.com";
        DomainInfo domainInfo = domainInfoService.getDomainInfo(testUrl);

        if (domainInfo != null) {
            return new ResponseEntity<>(domainInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to fetch domain info.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}