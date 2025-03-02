package com.reviewing.review.recommend.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class Embedding {

    @Value("${gpt.key}")
    private String OPENAI_API_KEY;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/embeddings";

    @PostMapping("/vector")
    public ResponseEntity<Map> createEmbedding(@RequestParam(value = "input") String input) {

        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.set("Content-Type", "application/json");

        // 요청 본문
        String requestBody = """
            {
                "model": "text-embedding-3-small",
                "input": "%s"
            }
        """.formatted(input);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // API 호출
        return restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
    }
}
