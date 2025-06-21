package com.reviewing.review.recommend.service;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchService {

    private final RestClient restClient;
    private final OpenSearchClient openSearchClient;
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    // openSearch 강의 존재 검색
    public boolean searchCourse(UUID courseId, String indexName) {
        try {
            GetResponse<Object> response = openSearchClient.get(g -> g
                    .index(indexName)
                    .id(String.valueOf(courseId))
                    .sourceExcludes("*"), Object.class);

            return response.found();
        } catch (IOException e) {
            log.info("실패: {}", courseId);
            throw new RuntimeException("OpenSearch 조회 실패 ", e);
        }
    }

    @Async
    public void createEmbeddingsAndSaveOpenSearch(JobParameters jobParameters) {
        try {
            jobLauncher.run(jobRegistry.getJob("CourseSaveToOpenSearchJob"), jobParameters);
        } catch (Exception e) {
            log.error("배치 실패", e);
        }
    }

    @Async
    public void updateCourseEmbeddingsToOpenSearch(JobParameters jobParameters) {
        try {
            jobLauncher.run(jobRegistry.getJob("UpdateCourseToOpenSearchJob"), jobParameters);
        } catch (Exception e) {
            log.error("배치 실패", e);
        }
    }

    // OpenSearch 인덱스 생성 -> 사용x
    public void createIndex(String indexName) {
        try {

            String mappingJson = """
            {
              "mappings": {
                "properties": {
                  "id": { "type": "keyword" },
                  "embedding": { "type": "object" }  // 벡터를 일반 JSON 배열로 저장
                }
              }
            }
            """;

            Request request = new Request("PUT", "/" + indexName);
            request.setJsonEntity(mappingJson);
            Response response = restClient.performRequest(request);

            System.out.println("Index created: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("인덱스 생성 실패");
        }
    }

    // OpenSearch 인덱스 삭제 -> 사용x
    public void deleteIndex(String indexName) {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
                    .index(indexName).build();
            openSearchClient.indices().delete(deleteIndexRequest);
        } catch (Exception e) {
            log.info("인덱스 존재x");
        }
    }

    // OpenSearch field 제한 변경 -> 사용x
    public void updateFieldLimit(String indexName) {
        try {
            String settingsJson = """
        {
          "index.mapping.total_fields.limit": 2000
        }
        """;

            Request request = new Request("PUT", "/" + indexName + "/_settings");
            request.setJsonEntity(settingsJson);

            Response response = restClient.performRequest(request);
            System.out.println("Field limit updated: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
