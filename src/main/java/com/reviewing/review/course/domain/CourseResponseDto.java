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
    private int wishes;
    private int comments;
    private boolean wished = false;

    public CourseResponseDto(Long id, String title, String teacher, String thumbnailImage,
            String thumbnailVideo, float rating, String slug, String url, int wishes, int comments) {
        this.id = id;
        this.title = title;
        this.teacher=teacher;
        this.thumbnailImage=thumbnailImage;
        this.thumbnailVideo=thumbnailVideo;
        this.rating=rating;
        this.slug=slug;
        this.url=url;
        this.wishes = wishes;
        this.comments = comments;
    }

}
