package com.reviewing.review.crawling.domain;
import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryCourseDto {

    private Category category;
    private Course course;

}