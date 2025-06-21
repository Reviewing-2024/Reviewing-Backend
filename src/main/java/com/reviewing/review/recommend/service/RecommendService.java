package com.reviewing.review.recommend.service;

import com.reviewing.review.recommend.domain.RecommendResponseDto;
import com.reviewing.review.recommend.domain.SearchResponseDto;
import com.reviewing.review.recommend.repository.RecommendRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final OpenSearchClient openSearchClient;
    private final RecommendRepository recommendRepository;

    public List<SearchResponseDto> search(String indexName, List<Double> queryVector, int size) {
        try {
            float[] vector = new float[queryVector.size()];
            for (int i = 0; i < queryVector.size(); i++) {
                vector[i] = queryVector.get(i).floatValue();
            }

            SearchResponse<Map> response = openSearchClient.search(s -> s
                            .index(indexName)
                            .size(size)
                            .query(q -> q
                                    .knn(knn -> knn
                                            .field("embedding")
                                            .k(5)
                                            .vector(vector)
                                    )
                            ),
                    Map.class
            );

            return response.hits().hits().stream()
                    .map(hit -> {
                        Map<String, Object> source = hit.source();
                        SearchResponseDto dto = new SearchResponseDto();
                        dto.setId(UUID.fromString((String) source.get("id")));
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("강의 추천 에러 발생");
            log.info(e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<RecommendResponseDto> findCourseBySearchResponses(List<SearchResponseDto> searchResponses) {
        List<RecommendResponseDto> result = new ArrayList<>();

        for (SearchResponseDto searchResponse : searchResponses) {
            RecommendResponseDto recommendResponseDto = recommendRepository.findCourseBySearchResponses(
                    searchResponse.getId());

            result.add(recommendResponseDto);
        }
        return result;
    }
}
