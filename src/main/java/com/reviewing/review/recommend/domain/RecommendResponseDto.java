package com.reviewing.review.recommend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendResponseDto {

    private String courseTitle;
    private String courseTeacher;
    private String courseUrl;
    private String reviewingUrl;
    private String courseSlug;

    public RecommendResponseDto(String courseTitle, String courseTeacher, String courseUrl,
            String courseSlug) {
        this.courseTitle = courseTitle;
        this.courseTeacher = courseTeacher;
        this.courseUrl = courseUrl;
        this.courseSlug = courseSlug;
    }

}
