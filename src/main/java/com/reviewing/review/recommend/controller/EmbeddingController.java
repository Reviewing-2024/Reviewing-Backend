package com.reviewing.review.recommend.controller;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.recommend.repository.RecommendRepository;
import com.reviewing.review.recommend.service.EmbeddingService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmbeddingController {

    private final EmbeddingService embeddingService;
    private final RecommendRepository recommendRepository;

    private final OpenSearchClient openSearchClient;

    // 벡터값 생성 후 OpenSearch에 저장
    // 백엔드에서만 사용
    @PostMapping("/embedding")
    public String createAndSaveEmbedding() {

        for (int i = 3; i <= 130; i++) {
            Course findCourse = recommendRepository.findCourseById((long) i);

            if (findCourse == null) {
                continue;
            }

            String text = String.format(
                    "Title: %s, Platform: %s, Category: %s, Rating: %.1f",
                    findCourse.getTitle(),
                    findCourse.getPlatform(),
                    findCourse.getCategory(),
                    findCourse.getRating()
            );

            List<Double> embedding = embeddingService.generateEmbedding(text);

            save("course", findCourse.getId(), embedding);

        }

        return "성공";

    }

    public void save(String indexName, Long id, List<Double> embedding) {
        try {
            // List<Double> → JSON Object 변환
            Map<String, Object> embeddingMap = new HashMap<>();
            for (int i = 0; i < embedding.size(); i++) {
                embeddingMap.put("dim_" + i, embedding.get(i)); // dim_0, dim_1, ...
            }

            // 문서 데이터 생성
            Map<String, Object> document = new HashMap<>();
            document.put("id", id);
            document.put("embedding", embeddingMap);

            // OpenSearch에 데이터 저장
            IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(builder -> builder
                    .index(indexName)   // 인덱스 이름
                    .id(String.valueOf(id)) // 문서 ID
                    .document(document) // 변환된 문서 데이터
            );

            IndexResponse response = openSearchClient.index(indexRequest);
            System.out.println("Document indexed: " + response.result());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
