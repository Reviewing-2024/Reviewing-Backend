package com.reviewing.review.review.service;

import com.reviewing.review.review.entity.Review;
import com.reviewing.review.review.entity.ReviewDislike;
import com.reviewing.review.review.entity.ReviewLike;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.entity.ReviewState;
import com.reviewing.review.review.domain.ReviewStateByMemberDto;
import com.reviewing.review.review.domain.ReviewStateType;
import com.reviewing.review.review.repository.ReviewRepository;
import com.reviewing.review.admin.service.AdminService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SlackService slackService;
    private final AdminService adminService;

    public void createReview(UUID courseId, Long memberId, ReviewRequestDto reviewRequestDto,
            String certification) {

        ReviewState reviewState = new ReviewState(ReviewStateType.PENDING);

        Review review = Review.builder()
                .contents(reviewRequestDto.getContents())
                .rating(reviewRequestDto.getRating())
                .createdAt(LocalDateTime.now())
                .certification(certification)
                .build();

        Review newReview = reviewRepository.createReview(courseId, memberId, reviewState, review);
        slackService.sendMessageToSlack(newReview);
    }

    public List<ReviewResponseDto> findReviewsByCourse(UUID courseId) {
        return reviewRepository.findReviewsByCourse(courseId);
    }

    public List<ReviewResponseDto> findReviewsWithLikedAndDislikedByCourse(UUID courseId,
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

            boolean isMyReview = reviewRepository.isReviewWrittenByMember(review.getId(), memberId);
            review.setMyReview(isMyReview);
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

        // 현재 사용자가 작성한 리뷰인지 확인
        boolean isMyReview = reviewRepository.isReviewWrittenByMember(reviewId, memberId);
        reviewResponseDto.setMyReview(isMyReview);

        return reviewResponseDto;
    }

    public boolean checkReviewLikedByMember(Long reviewId, Long memberId) {
        ReviewLike findReviewLike = reviewRepository.checkReviewLiked(reviewId, memberId);
        return findReviewLike != null;
    }

    public boolean checkReviewDislikedByMember(Long reviewId, Long memberId) {
        ReviewDislike findReviewDislike = reviewRepository.checkReviewDisliked(reviewId, memberId);
        return findReviewDislike != null;
    }

    public int checkBeforeReviewCreate(UUID courseId, Long memberId) {
        ReviewStateByMemberDto findReview = reviewRepository.findReviewByCourseIdAndMemberId(courseId,
                memberId);
        if (findReview == null) {
            return 200;
        }
        if (findReview.getReviewState() == ReviewStateType.REJECTED) {
            return 200;
        }
        if (findReview.getReviewState() == ReviewStateType.PENDING) {
            return 601;
        }
        // reviewState == ReviewStateType.APPROVED
        return 602;
    }

    public void softDeleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findReviewByReviewId(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("리뷰를 찾을 수 없습니다.");
        }
        
        if (!review.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        
        if (review.isDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 리뷰입니다.");
        }

        UUID courseId = review.getCourse().getId();
        BigDecimal reviewRating = review.getRating();
        adminService.updateCourseRating(courseId, reviewRating, false);
        
        reviewRepository.softDeleteReview(reviewId);
    }

}
