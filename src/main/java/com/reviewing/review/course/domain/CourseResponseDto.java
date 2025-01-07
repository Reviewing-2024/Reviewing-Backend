package com.reviewing.review.course.domain;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDto {

    private UUID id;
    private String title;
    private String teacher;
    private String thumbnailImage;
    private String thumbnailVideo;
    private BigDecimal rating;
    private String slug;
    private String url;
    private int wishes;
    private int comments;
    private boolean wished = false;

    public CourseResponseDto(UUID id, String title, String teacher, String thumbnailImage,
            String thumbnailVideo, BigDecimal rating, String slug, String url, int wishes, int comments) {
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
