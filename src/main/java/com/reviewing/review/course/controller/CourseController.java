package com.reviewing.review.course.controller;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/")
    public ResponseEntity<List<Course>> getCourses () {

        List<Course> courses = courseService.getAllCourses();

        return ResponseEntity.ok().body(courses);

    }

}
