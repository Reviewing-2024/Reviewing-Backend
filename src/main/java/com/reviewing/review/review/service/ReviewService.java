package com.reviewing.review.review.service;

import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewDislike;
import com.reviewing.review.review.domain.ReviewLike;
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

        List<ReviewResponseDto> reviews = reviewRepository.findReviewsByCourse(courseId);

        for (ReviewResponseDto review : reviews) {
            long findReviewDislikes = reviewRepository.findReviewDislikes(review.getId());
            review.setDislikes(findReviewDislikes);
        }

        return reviews;
    }

    public List<ReviewResponseDto> findReviewsWithLikedAndDislikedByCourse(Long courseId,
            Long memberId) {

        List<ReviewResponseDto> reviews = reviewRepository.findReviewsWithLikedAndDislikedByCourse(courseId);

        for (ReviewResponseDto review : reviews) {
            ReviewLike findReviewLike = reviewRepository.checkReviewLiked(review.getId(), memberId);
            ReviewDislike findReviewDislike = reviewRepository.checkReviewDisliked(review.getId(),
                    memberId);

            if (findReviewLike != null) {
                review.setLiked(true);
            }

            if (findReviewDislike != null) {
                review.setDisliked(true);
            }

            long findReviewDislikes = reviewRepository.findReviewDislikes(review.getId());
            review.setDislikes(findReviewDislikes);

        }

        return reviews;
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
        ReviewResponseDto reviewResponseDto =  reviewRepository.findReviewById(reviewId);

        ReviewLike findReviewLike = reviewRepository.checkReviewLiked(reviewId, memberId);
        ReviewDislike findReviewDislike = reviewRepository.checkReviewDisliked(reviewId, memberId);
        if (findReviewLike != null) {
            reviewResponseDto.setLiked(true);
        }
        if (findReviewDislike != null) {
            reviewResponseDto.setDisliked(true);
        }

        long reviewDislikes = (long) reviewRepository.findReviewDislikes(reviewId);
        reviewResponseDto.setDislikes(reviewDislikes);

        return reviewResponseDto;
    }

}
