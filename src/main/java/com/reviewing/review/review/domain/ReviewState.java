package com.reviewing.review.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ReviewState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reviewId;
    @Enumerated(EnumType.STRING)
    private ReviewStateType state;
    private String rejectionReason;

    @Builder
    public ReviewState(Long reviewId, ReviewStateType state, String rejectionReason) {
        this.reviewId = reviewId;
        this.state = state;
        this.rejectionReason = rejectionReason;
    }

}

