package com.reviewing.review.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Course {

    @Id
    private Long id;

    @ManyToOne
    private Platform platform;

    @ManyToOne
    private Category category;

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
    @Column(columnDefinition = "integer default 0")
    private int wishes;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean updated;

}
