package com.product.uptime.service;


import com.product.uptime.entity.ErrorCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
                    postUrl+"add_monitor",
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
    public String sendDeleteRequest(String id) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Use exchange method instead of the specific delete method
            // This allows us to send a body with the DELETE request
            ResponseEntity<String> response = restTemplate.exchange(
                    postUrl + "/delete_monitor", // Assuming there's a delete endpoint
                    HttpMethod.DELETE,
                    request,
                    String.class
            );

            System.out.println("Delete response status code: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in delete request: " + e.getMessage());
            return "Error occurred during deletion: " + e.getMessage();
        }
    }
}



