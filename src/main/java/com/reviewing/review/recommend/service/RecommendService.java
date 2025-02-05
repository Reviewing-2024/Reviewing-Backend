package com.reviewing.review.recommend.service;

import com.reviewing.review.recommend.domain.RecommendResponseDto;
import com.reviewing.review.recommend.domain.SearchResponseDto;
import com.reviewing.review.recommend.repository.RecommendRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final OpenSearchClient openSearchClient;
    private final RecommendRepository recommendRepository;

    public List<SearchResponseDto> search(String indexName, List<Double> queryVector, int size) {
        try {
            // 스크립트 파라미터 설정
            Map<String, JsonData> params = new HashMap<>();
            for (int i = 0; i < queryVector.size(); i++) {
                params.put("query_dim_" + i, JsonData.of(queryVector.get(i))); // dim_0, dim_1, ...
            }

            // 스크립트 정의
            String cosineSimilarityScript = """
                double dotProduct = 0.0;
                double queryNorm = 0.0;
                double docNorm = 0.0;
            
                int dimensions = 1536;
    
                for (int i = 0; i < dimensions; i++) {
                    double queryValue = params.get("query_dim_" + i);
                    double docValue = doc['embedding.dim_' + i].value;
    
                    dotProduct += queryValue * docValue;
                    queryNorm += queryValue * queryValue;
                    docNorm += docValue * docValue;
                }
    
                return dotProduct / (Math.sqrt(queryNorm) * Math.sqrt(docNorm));
            """;

            // OpenSearch 검색 요청 생성
            SearchRequest searchRequest = SearchRequest.of(builder -> builder
                    .index(indexName) // 검색할 인덱스 이름
                    .query(q -> q
                            .scriptScore(script -> script
                                    .query(matchAll -> matchAll.matchAll(m -> m)) // 모든 문서에서 검색
                                    .script(s -> s.inline(inline -> inline
                                            .source(cosineSimilarityScript) // 코사인 유사도 계산 스크립트
                                            .params(params) // 스크립트 파라미터 전달
                                    ))
                            )
                    )
                    .source(source -> source.filter(filter -> filter.includes("id")))
                    .size(size) // 반환할 결과 개수
            );

            // OpenSearch 클라이언트를 사용해 검색 실행
            SearchResponse<SearchResponseDto> response = openSearchClient.search(searchRequest, SearchResponseDto.class);

//            System.out.println("Hits:");
//            response.hits().hits().forEach(hit -> {
//                System.out.println("Source: " + hit.source());
//                System.out.println("Score: " + hit.score());
//            });

            // 결과 반환
            return response.hits().hits().stream()
                    .map(Hit::source) // `_source`에서 문서 데이터 추출
                    .toList();

//            return response.hits().hits().stream()
//                    .map(hit -> {
//                        SearchResponseDto test = hit.source();
//                        test.setScore(hit.score()); // `_score` 값을 Test 객체에 설정
//                        return test;
//                    })
//                    .toList();

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
