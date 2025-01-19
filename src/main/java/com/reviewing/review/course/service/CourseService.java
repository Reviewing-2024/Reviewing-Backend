package com.reviewing.review.course.service;

import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.entity.CourseWish;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.course.repository.CourseRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

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
            String category, String sortType, UUID lastCourseId, BigDecimal lastRating,
            Integer lastComments) {

        return courseRepository.findCoursesByPlatformAndCategory(platform, category, sortType,
                lastCourseId, lastRating, lastComments);
    }

    public void createCourseWish(UUID courseId, Long memberId) {
        courseRepository.createCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
        courseRepository.updateCourseWishCount(courseId, true);
    }

    public void removeCourseWish(UUID courseId, Long memberId) {
        courseRepository.removeCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
        courseRepository.updateCourseWishCount(courseId, false);
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

    public List<CourseResponseDto> searchCoursesByKeyword(String keyword, UUID lastCourseId) {
        return courseRepository.searchCoursesByKeyword(keyword, lastCourseId);
    }

    public CourseResponseDto checkCourseWished(CourseResponseDto courseResponseDto, Long memberId) {
        CourseWish findCourseWish = courseRepository.findCourseWish(courseResponseDto.getId(), memberId);
        if (findCourseWish != null) {
            courseResponseDto.setWished(true);
        }
        return courseResponseDto;
    }

}
