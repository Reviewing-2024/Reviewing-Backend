package com.reviewing.review.course.service;

import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseRequestDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.CourseWish;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.course.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    // 전체 강의 조회
    public List<CourseResponseDto> findAllCoursesBySorting(CourseRequestDto courseRequestDto) {
        return courseRepository.findAllCoursesBySorting(courseRequestDto.getSort(),
                courseRequestDto.getLastCourseId(), courseRequestDto.getLastRating(),
                courseRequestDto.getLastComments());
    }
    // 플랫폼 기준 조회
    public List<CourseResponseDto> findCoursesByPlatform(String platform, CourseRequestDto courseRequestDto) {
        return courseRepository.findCoursesByPlatform(platform, courseRequestDto.getSort(),
                courseRequestDto.getLastCourseId(), courseRequestDto.getLastRating(),
                courseRequestDto.getLastComments());
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, CourseRequestDto courseRequestDto) {
        return courseRepository.findCoursesByPlatformAndCategory(platform, category, courseRequestDto.getSort(),
                courseRequestDto.getLastCourseId(), courseRequestDto.getLastRating(),
                courseRequestDto.getLastComments());
    }

    public void createCourseWish(Long courseId, Long memberId) {
        courseRepository.createCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
        courseRepository.updateCourseWishCount(courseId, true);
    }

    public void removeCourseWish(Long courseId, Long memberId) {
        courseRepository.removeCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
        courseRepository.updateCourseWishCount(courseId, false);
    }

    public CourseResponseDto findCourseById(Long courseId, Long memberId) {

        CourseResponseDto courseResponseDto = courseRepository.findCourseById(courseId, memberId);

        CourseWish findCourseWish = courseRepository.checkCourseWish(courseId, memberId);

        if (findCourseWish != null) {
            courseResponseDto.setWished(true);
        }

        return courseResponseDto;
    }

    public List<Platform> findPlatforms() {
        return courseRepository.findPlatforms();
    }

    public List<CategoryResponseDto> findCategories(String platform) {
        return courseRepository.findCategories(platform);
    }

    public List<CourseResponseDto> checkCourseWished(List<CourseResponseDto> courses,
            Long memberId) {

        for (CourseResponseDto course : courses) {
            CourseWish findCourseWish = courseRepository.findCourseWish(course.getId(), memberId);

            if (findCourseWish != null) {
                course.setWished(true);
            }
        }
        return courses;
    }
}
