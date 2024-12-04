package com.reviewing.review.course.controller;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/")
    public ResponseEntity<List<CourseResponseDto>> findAllCoursesBySorting (@RequestParam(value = "sort",required = false) String sortType) {
        List<CourseResponseDto> courses = courseService.findAllCoursesBySorting(sortType);
        return ResponseEntity.ok().body(courses);
    }

    @GetMapping("/courses/{platform}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatform(@PathVariable String platform,
            @RequestParam(value = "sort",required = false) String sortType) {
        List<CourseResponseDto> courses = courseService.findCoursesByPlatform(platform, sortType);
        return ResponseEntity.ok().body(courses);
    }

    @GetMapping("/courses/{platform}/{category}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatformAndCategory(
            @PathVariable("platform") String platform, @PathVariable("category") String category,
            @RequestParam(value = "sort",required = false) String sortType) {
        List<CourseResponseDto> courses = courseService.findCoursesByPlatformAndCategory(platform,
                category, sortType);
        return ResponseEntity.ok().body(courses);
    }

}
