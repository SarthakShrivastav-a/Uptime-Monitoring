package com.product.uptime.service;

import com.product.uptime.entity.DomainInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URI;

@Service
public class DomainInfoService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${whois.api.key}")
    private String whoisApiKey;

    @Value("${whois.api.url}")
    private String whoisApiBaseUrl;

    public DomainInfo getDomainInfo(String url) {
        try {
            String domain = extractDomain(url);
            String apiUrl = whoisApiBaseUrl + "?apiKey=" + whoisApiKey + "&domainName=" + domain + "&outputFormat=JSON";

            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            // Log response for debugging
            System.out.println("API Response: " + response.getBody());

            // Check if response is not empty
            if (response.getBody() == null || response.getBody().isEmpty()) {
                System.err.println("Empty response from API");
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Check if WhoisRecord exists
            if (!jsonResponse.has("WhoisRecord")) {
                System.err.println("Invalid API response: Missing 'WhoisRecord' field");
                return null;
            }

            JSONObject whoisRecord = jsonResponse.getJSONObject("WhoisRecord");
            if (!whoisRecord.has("expiresDate")) {
                System.err.println("No expiry date found in response.");
                return null;
            }

            // Parse expiry date
            String expiryDateStr = whoisRecord.getString("expiresDate");
            Instant expiryInstant = parseDateTime(expiryDateStr);
            Instant reminderInstant = expiryInstant.minusSeconds(30L * 24 * 60 * 60); // 1 month before expiry

            DomainInfo domainInfo = new DomainInfo();
            domainInfo.setDomainExpiry(expiryInstant);
            domainInfo.setRemindingDate(reminderInstant);

            // Determine status
            Instant currentInstant = Instant.now();
            if (currentInstant.isBefore(reminderInstant)) {
                domainInfo.setStatus("VALID");
            } else if (currentInstant.isBefore(expiryInstant)) {
                domainInfo.setStatus("EXPIRING_SOON");
            } else {
                domainInfo.setStatus("EXPIRED");
            }

            return domainInfo;

        } catch (Exception e) {
            System.err.println("Error retrieving domain info: " + e.getMessage());
            return null;
        }
    }

    private Instant parseDateTime(String dateTimeStr) {
        try {
            return Instant.parse(dateTimeStr);
        } catch (Exception e) {
            try {
                if (dateTimeStr.charAt(19) == '+') {
                    String formattedDate = dateTimeStr.substring(0, 19) + "+" + dateTimeStr.substring(20, 22) + ":" + dateTimeStr.substring(22);
                    return ZonedDateTime.parse(formattedDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to parse expiry date: " + dateTimeStr, e);
            }
        }
        throw new RuntimeException("Failed to parse expiry date: " + dateTimeStr);
    }


    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost().replace("www.", "");
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL format: " + url, e);
        }
    }
}

