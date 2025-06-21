package com.reviewing.review.recommend.controller;

import com.reviewing.review.recommend.domain.RecommendRequestDto;
import com.reviewing.review.recommend.domain.RecommendResponseDto;
import com.reviewing.review.recommend.domain.SearchResponseDto;
import com.reviewing.review.recommend.service.EmbeddingService;
import com.reviewing.review.recommend.service.RecommendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecommendController {

    private final RecommendService recommendService;
    private final EmbeddingService embeddingService;

    @Value("${opensearch.index}")
    private String INDEX;

    @PostMapping("/recommendation")
    public List<RecommendResponseDto> recommendCourse(
            @RequestBody RecommendRequestDto recommendRequestDto) {

        List<SearchResponseDto> searchResponses = recommendService.search(INDEX,
                embeddingService.generateEmbeddingV2(recommendRequestDto.getQuestion()), 5);

        return recommendService.findCourseBySearchResponses(searchResponses);

    }

}
