package com.product.uptime.controller;

import com.product.uptime.entity.SSLInfo;
import com.product.uptime.service.DomainInfoService;
import com.product.uptime.entity.DomainInfo;
import com.product.uptime.service.SSLInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestDomainInfo {

    private final DomainInfoService domainInfoService;

    public TestDomainInfo(DomainInfoService domainInfoService) {
        this.domainInfoService = domainInfoService;
    }
    @Autowired
    private  SSLInfoService sslInfoService;

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
    @GetMapping("/check")
    public ResponseEntity<SSLInfo> checkSSLInfo() {

        String url = "https://google.com";
        SSLInfo sslInfo = sslInfoService.getSSLInfo(url);

        if (sslInfo == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(sslInfo);
    }
}