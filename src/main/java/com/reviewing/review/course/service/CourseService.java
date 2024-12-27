package com.reviewing.review.course.service;

import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.CourseWish;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.course.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    // 전체 강의 조회
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("getAllCourses");

        List<CourseResponseDto> courses = courseRepository.findAllCoursesBySorting(sortType);

        for (CourseResponseDto course : courses) {

            long courseWishes = courseRepository.findCourseWishes(course.getId());
            course.setWishes(courseWishes);
        }

        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return courses;
    }

    public List<CourseResponseDto> findAllCoursesBySorting(String sortType, Long memberId) {
        return courseRepository.findAllCoursesBySorting(sortType, memberId);
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType) {
        return courseRepository.findCoursesByPlatform(platform, sortType);
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType,
            Long memberId) {

        return courseRepository.findCoursesByPlatform(platform, sortType, memberId);
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType) {
        return courseRepository.findCoursesByPlatformAndCategory(platform, category, sortType);
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType, Long memberId) {
        return courseRepository.findCoursesByPlatformAndCategory(platform, category, sortType,
                memberId);
    }

    public void createCourseWish(Long courseId, Long memberId) {
        courseRepository.createCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
    }

    public void removeCourseWish(Long courseId, Long memberId) {
        courseRepository.removeCourseWish(courseId, memberId);

        courseRepository.changeCourseUpdated(courseId);
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
}
