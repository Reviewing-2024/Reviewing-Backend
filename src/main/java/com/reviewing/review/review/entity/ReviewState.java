package com.reviewing.review.review.entity;

import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ReviewState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReviewStateType state;
    private String rejectionReason;
    private LocalDateTime updatedAt;

    public ReviewState( ReviewStateType state) {
        this.state = state;
    }

}

