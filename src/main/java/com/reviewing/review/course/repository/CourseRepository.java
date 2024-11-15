package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}

