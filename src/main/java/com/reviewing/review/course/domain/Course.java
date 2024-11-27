package com.reviewing.review.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Course {

    @Id
    private Long id;

    private Long platformId;
    private Long categoryId;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String url;
    @Column(columnDefinition = "text")
    private String thumbnailImage;
    @Column(columnDefinition = "text")
    private String thumbnailVideo;
    private String teacher;
    private String slug;
    private float rating;

}
