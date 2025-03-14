package com.product.uptime.service;

import com.product.uptime.entity.ErrorCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {

    private final WebClient webClient;

    @Autowired
    public PostService(WebClient webClient) {
        this.webClient = webClient;
    }
    @Value("${post.url.go}")
    private String postUrl;

    public Mono<String> sendPostRequest(String id , String url , ErrorCondition errorCondition) {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("monitor_id", id);
        requestBody.put("url", url);
        requestBody.put("error_condition", errorCondition);

        return webClient.post()
                .uri(postUrl)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}
