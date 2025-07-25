package com.reviewing.review.course.service;

import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.entity.CourseWish;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.course.repository.CourseRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final OpenSearchClient openSearchClient;

    @Value("${opensearch.index}")
    private String INDEX;

    // 전체 강의 조회
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType, UUID lastCourseId,
            BigDecimal lastRating, Integer lastComments) {

        return courseRepository.findAllCoursesBySorting(sortType,
                lastCourseId, lastRating, lastComments);
    }
    // 플랫폼 기준 조회
    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType, UUID lastCourseId,
            BigDecimal lastRating, Integer lastComments) {
        return courseRepository.findCoursesByPlatform(platform, sortType,
                lastCourseId, lastRating, lastComments);
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String categorySlug, String sortType, UUID lastCourseId, BigDecimal lastRating,
            Integer lastComments) {

        return courseRepository.findCoursesByPlatformAndCategory(platform, categorySlug, sortType,
                lastCourseId, lastRating, lastComments);
    }

    public void createCourseWish(UUID courseId, Long memberId) {
        courseRepository.createCourseWish(courseId, memberId);
    }

    public void removeCourseWish(UUID courseId, Long memberId) {
        courseRepository.removeCourseWish(courseId, memberId);
    }

    public CourseResponseDto findCourseById(UUID courseId) {

        return courseRepository.findCourseById(courseId);
    }

    public CourseResponseDto findCourseBySlug(String courseSlug) {
        return courseRepository.findCourseBySlug(courseSlug);
    }

    public List<Platform> findPlatforms() {
        return courseRepository.findPlatforms();
    }

    public List<CategoryResponseDto> findCategories(String platform) {
        return courseRepository.findCategories(platform);
    }

    public List<CourseResponseDto> checkCoursesWished(List<CourseResponseDto> courses,
            Long memberId) {

        for (CourseResponseDto course : courses) {
            CourseWish findCourseWish = courseRepository.findCourseWish(course.getId(), memberId);

            if (findCourseWish != null) {
                course.setWished(true);
            }
        }
        return courses;
    }

    public boolean checkCourseWishedByMember(UUID courseId, Long memberId) {
        CourseWish findCourseWish = courseRepository.findCourseWish(courseId, memberId);
        return findCourseWish != null;
    }

//    public List<CourseResponseDto> searchCoursesByKeyword(String keyword, UUID lastCourseId) {
//        return courseRepository.searchCoursesByKeyword(keyword, lastCourseId);
//    }

    public List<UUID> searchCoursesByKeyword(String keyword, UUID lastCourseId) {
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(INDEX)
                    .size(20)
                    .query(q -> q
                            .multiMatch(m -> m
                                    .fields("title.korean", "title.english", "teacher.korean", "teacher.english")
                                    .query(keyword)
                                    .type(TextQueryType.MostFields)
                                    .fuzziness("AUTO")
                            )
                    )
                    .sort(sort -> sort
                            .field(f -> f
                                    .field("id")
                                    .order(SortOrder.Asc)
                            )
                    );

            if (lastCourseId != null) {
                builder.searchAfter(List.of(lastCourseId.toString()));
            }

            SearchResponse<Map> response = openSearchClient.search(builder.build(), Map.class);

            return response.hits().hits().stream()
                    .map(hit -> UUID.fromString((String) hit.source().get("id")))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("강의 검색 에러 발생");
            log.error("error", e);
            return Collections.emptyList();
        }

    }

    public CourseResponseDto checkCourseWished(CourseResponseDto courseResponseDto, Long memberId) {
        CourseWish findCourseWish = courseRepository.findCourseWish(courseResponseDto.getId(), memberId);
        if (findCourseWish != null) {
            courseResponseDto.setWished(true);
        }
        return courseResponseDto;
    }

}
