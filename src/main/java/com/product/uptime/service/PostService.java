package com.product.uptime.service;


import com.product.uptime.entity.ErrorCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {
    private final RestTemplate restTemplate;

    @Value("${post.url.go}")
    private String postUrl;

    public PostService() {
        this.restTemplate = new RestTemplate();
    }

    public String sendPostRequest(String id, String url , ErrorCondition er) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("monitor_id",id);
        requestBody.put("url",url);

        Map<String, Object> errorCondition = new HashMap<>();
        errorCondition.put("triggerOn", er.getTriggerOn());
        errorCondition.put("value", er.getValue());

        requestBody.put("error_condition", errorCondition);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    postUrl,
                    request,
                    String.class
            );

            System.out.println("Response status code: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in request: " + e.getMessage());
            return "Error occurred: " + e.getMessage();
        }
    }
}



