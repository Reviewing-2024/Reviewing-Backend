package com.reviewing.review.review.service;

import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.domain.ReviewState;
import com.reviewing.review.review.domain.ReviewStateType;
import com.reviewing.review.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void createReview(Long courseId, Long memberId, ReviewRequestDto reviewRequestDto,
            String certification) {

        ReviewState reviewState = new ReviewState(ReviewStateType.PENDING);

        Review review = Review.builder()
                .contents(reviewRequestDto.getContents())
                .rating(reviewRequestDto.getRating())
                .createdAt(LocalDateTime.now())
                .certification(certification)
                .build();

        reviewRepository.createReview(courseId, memberId, reviewState, review);
    }

    public List<ReviewResponseDto> findReviewsByCourse(Long courseId) {
        return reviewRepository.findReviewsByCourse(courseId);
    }

    public List<ReviewResponseDto> findReviewsWithLikedAndDislikedByCourse(Long courseId,
            Long memberId) {
        return reviewRepository.findReviewsWithLikedAndDislikedByCourse(courseId, memberId);
    }

    public void createReviewLike(Long reviewId, Long memberId) {
        reviewRepository.createReviewLike(reviewId, memberId);
    }

    public void removeReviewLike(Long reviewId, Long memberId) {
        reviewRepository.removeReviewLike(reviewId, memberId);
    }

    public void createReviewDislike(Long reviewId, Long memberId) {
        reviewRepository.createReviewDislike(reviewId, memberId);
    }

    public void removeReviewDislike(Long reviewId, Long memberId) {
        reviewRepository.removeReviewDislike(reviewId, memberId);
    }

    public ReviewResponseDto findReviewById(Long reviewId, Long memberId) {
        return reviewRepository.findReviewById(reviewId, memberId);
    }

}
