package com.reviewing.review.review.domain;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @ManyToOne
    private Member member;

    @ManyToOne
    private Course course;

    @Column(columnDefinition = "text")
    private String contents;
    private float rating;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne
    private ReviewState reviewState;

    @Column(columnDefinition = "text")
    private String certification;

    @Builder
    public Review(String contents, float rating, LocalDateTime createdAt, String certification) {
        this.contents = contents;
        this.rating = rating;
        this.createdAt = createdAt;
        this.certification = certification;
    }

}
