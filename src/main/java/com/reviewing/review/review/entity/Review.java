package com.reviewing.review.review.entity;

import com.reviewing.review.course.entity.Course;
import com.reviewing.review.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @Column(columnDefinition = "text")
    private String contents;
    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal rating;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne
    private ReviewState reviewState;

    @Column(columnDefinition = "text")
    private String certification;

    @Column(nullable = false)
    private int likes = 0 ;
    @Column(nullable = false)
    private int dislikes = 0;

    @Column(nullable = false)
    private boolean isDeleted = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    @Builder
    public Review(String contents, BigDecimal rating, LocalDateTime createdAt, String certification) {
        this.contents = contents;
        this.rating = rating;
        this.createdAt = createdAt;
        this.certification = certification;
    }

}
