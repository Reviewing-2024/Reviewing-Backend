package com.reviewing.review.crawling.domain;

import com.reviewing.review.course.entity.Platform;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrawlingCourseDto {

    private String categorySlug;
    private Platform platform;
    private String title;
    private String courseUrl;
    private String thumbnailImage;
    private String thumbnailVideo;
    private String teacher;
    private String courseSlug;

    @Builder
    public CrawlingCourseDto(String categorySlug, Platform platform, String title, String courseUrl,
            String thumbnailImage, String thumbnailVideo, String teacher, String courseSlug) {
        this.categorySlug = categorySlug;
        this.platform = platform;
        this.title = title;
        this.courseUrl = courseUrl;
        this.thumbnailImage = thumbnailImage;
        this.thumbnailVideo = thumbnailVideo;
        this.teacher = teacher;
        this.courseSlug = courseSlug;
    }

}
