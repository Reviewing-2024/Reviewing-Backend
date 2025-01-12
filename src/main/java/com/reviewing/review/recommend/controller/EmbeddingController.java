package com.reviewing.review.recommend.controller;

import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.recommend.service.EmbeddingService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    private final OpenSearchClient openSearchClient;

    // 벡터값 생성 후 OpenSearch에 저장
    // 백엔드에서만 사용
    @PostMapping("/embedding")
    public String createAndSaveAllCourseEmbedding() {
        List<Course> courses = courseCrawlingRepository.findAll();

        for (Course course : courses) {
            String title = course.getTitle();
            String platform = course.getPlatform().getName();
            BigDecimal rating = course.getRating();
            int wishes = course.getWishes();
            List<CategoryCourse> categories = categoryCourseRepository.findByCourse(course);
            String categoriesText = categories.stream()
                    .map(category -> category.getCategory().getName())
                    .collect(Collectors.joining(", "));

            String inputCourseText = String.format(
                    "Title: %s, Platform: %s, Category: %s, Rating: %.1f, Wishes: %d",
                    title,
                    platform,
                    categoriesText,
                    rating,
                    wishes
            );

            List<Double> embedding = embeddingService.generateEmbedding(inputCourseText);
            save("course", course.getId(), embedding);
        }
        return "성공";
    }

    public void save(String indexName, UUID id, List<Double> embedding) {
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
//            System.out.println("Document indexed: " + response.result());
        } catch (Exception e) {
            log.error("openSearch course 문서 저장 실패: {}", e.getMessage());
        }
    }

}
