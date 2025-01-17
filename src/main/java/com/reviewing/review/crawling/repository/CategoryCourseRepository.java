package com.reviewing.review.crawling.repository;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCourseRepository extends JpaRepository<CategoryCourse, Long> {

    Optional<CategoryCourse> findByCourseAndCategory(Course course, Category category);

    List<CategoryCourse> findByCourse(Course course);

}
