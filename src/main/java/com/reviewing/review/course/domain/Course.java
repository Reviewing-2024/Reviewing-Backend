package com.reviewing.review.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "course") // DB 테이블 이름과 매핑
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long platformId;
    private Long categoryId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT")
    private String thumbnailImage;
    @Column(columnDefinition = "TEXT")
    private String thumbnailVideo;
    private String teacher;
    private String slug;
    private Float rating;

}
