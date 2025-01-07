package com.reviewing.review.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Platform platform;

    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String url;
    @Column(columnDefinition = "text")
    private String thumbnailImage;
    @Column(columnDefinition = "text")
    private String thumbnailVideo;
    private String teacher;
    @Column(unique = true)
    private String slug;
    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(0.0);
    @Column(nullable = false)
    private int wishes = 0;
    @Column(nullable = false)
    private int comments = 0;
    @Column(nullable = false)
    private boolean updated = false;

}
