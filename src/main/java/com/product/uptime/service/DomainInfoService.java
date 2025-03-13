package com.product.uptime.service;

import com.product.uptime.entity.DomainInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DomainInfoService {

    private final RestTemplate restTemplate;

    @Value("${whois.api.key}")
    private String whoisApiKey;

    @Value("${whois.api.url}")
    private String whoisApiBaseUrl;

    public DomainInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DomainInfo getDomainInfo(String url) {
        try {
            String domain = extractDomain(url);
            String apiUrl = whoisApiBaseUrl + "?apiKey=" + whoisApiKey + "&domainName=" + domain + "&outputFormat=json";

            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            JSONObject json = new JSONObject(response.getBody());

            if (json.has("WhoisRecord")) {
                String expiryDateStr = json.getJSONObject("WhoisRecord").optString("expiresDate", null);
                if (expiryDateStr != null) {
                    Instant expiryInstant = parseDateTime(expiryDateStr);
                    DomainInfo domainInfo = new DomainInfo();
                    domainInfo.setDomainExpiry(expiryInstant);
                    return domainInfo;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch domain info for: " + url);
            e.printStackTrace();
        }
        return null;
    }

    private Instant parseDateTime(String dateTimeStr) {
        try {

            return Instant.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            try {

                if (dateTimeStr.length() > 19 && dateTimeStr.charAt(19) == '+') {
                    String formattedDate = dateTimeStr.substring(0, 19) +
                            "+" + dateTimeStr.substring(20, 22) +
                            ":" + dateTimeStr.substring(22, 24);
                    return Instant.parse(formattedDate);
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
                ZonedDateTime zdt = ZonedDateTime.parse(dateTimeStr, formatter);
                return zdt.toInstant();
            } catch (Exception ex) {
                System.err.println("Could not parse date: " + dateTimeStr);
                throw new RuntimeException("Failed to parse date: " + dateTimeStr, ex);
            }
        }
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null ? host : url.replace("https://", "").replace("http://", "").split("/")[0];
        } catch (Exception e) {
            return url.replace("https://", "").replace("http://", "").split("/")[0];
        }
    }
}