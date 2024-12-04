package com.reviewing.review.course.service;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
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

    public List<CourseResponseDto> findAllCoursesBySorting(String sortType) {
        return courseRepository.findAllCoursesBySorting(sortType);
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType) {
        return courseRepository.findCoursesByPlatform(platform, sortType);
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType) {
        return courseRepository.findCoursesByPlatformAndCategory(platform, category, sortType);
    }
}
