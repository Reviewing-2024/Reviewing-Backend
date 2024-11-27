package com.reviewing.review.course.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseResponseDto {

    private Long id;
    private String title;
    private String teacher;
    private String thumbnailImage;
    private String thumbnailVideo;
    private float rating;
    private String slug;
    private String url;

}
