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
    private Long wishes;
    private boolean wished;

    public CourseResponseDto(Long id, String title, String teacher, String thumbnailImage,
            String thumbnailVideo, float rating, String slug, String url, Long wishes) {
        this.id = id;
        this.title = title;
        this.teacher=teacher;
        this.thumbnailImage=thumbnailImage;
        this.thumbnailVideo=thumbnailVideo;
        this.rating=rating;
        this.slug=slug;
        this.url=url;
        this.wishes = wishes;
    }

}
