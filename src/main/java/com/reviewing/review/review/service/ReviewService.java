package com.reviewing.review.review.service;

import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.domain.ReviewState;
import com.reviewing.review.review.domain.ReviewStateType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    public void createReview(Long courseId, Long memberId, ReviewRequestDto reviewRequestDto) {
//        Review review = new Review(memberId, courseId, reviewRequestDto.getContents(),
//                reviewRequestDto.getRating(), LocalDateTime.now());
//
//        ReviewState reviewState = new ReviewState();
    }

    public ReviewStateType checkReviewState(String reviewState) {
        for (ReviewStateType value : ReviewStateType.values()) {
            if (value.getReviewState().equals(reviewState)) {
                return value;
            }
        }
        // 예외 처리
        return null;
    }

}
