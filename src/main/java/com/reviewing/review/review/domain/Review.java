package com.reviewing.review.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

    private Long memberId;
    private Long courseId;
    @Column(columnDefinition = "text")
    private String contents;
    private float rating;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdAt;
    // certification

    @Builder
    public Review(Long memberId, Long courseId, String contents, float rating, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.courseId = courseId;
        this.contents = contents;
        this.rating = rating;
        this.createdAt = createdAt;
    }

}
