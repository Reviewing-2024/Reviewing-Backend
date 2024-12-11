package com.reviewing.review.recommend.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    @Value("${gpt.key}")
    private String OPENAI_API_KEY;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/embeddings";

    public List<Double> generateEmbedding(String text) {
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
        """.formatted(text);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);

        // 응답 데이터 파싱
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new IllegalStateException("Response body is null");
        }

        // "data" 필드에서 첫 번째 항목의 "embedding" 값을 추출
        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("No embedding data found");
        }

        // 첫 번째 항목의 "embedding" 필드 추출
        List<Double> embeddingList = (List<Double>) data.get(0).get("embedding");
        if (embeddingList == null) {
            throw new IllegalStateException("No embedding field found");
        }

        // List<Double>를 double[]로 변환
        return embeddingList;
    }


}
