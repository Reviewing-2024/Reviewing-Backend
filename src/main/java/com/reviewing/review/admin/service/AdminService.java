package com.reviewing.review.admin.service;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.admin.repository.AdminRepository;
import com.reviewing.review.review.entity.Review;
import com.reviewing.review.review.domain.ReviewStateType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.reviewing.review.course.entity.Course;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;

    public List<AdminReviewResponseDto> findReviewByStatus(String status) {
        return adminRepository.findReviewByStatus(checkReviewState(status));
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

    public void changeReviewApprove(Long reviewId) {

        Review findReview = adminRepository.findReviewById(reviewId); // 새로 등록한 리뷰

        BigDecimal thisReviewRating = findReview.getRating(); // 새로 등록한 리뷰 평점
        UUID courseId = findReview.getCourse().getId();

        // 공통 평점 계산 메서드 사용 (리뷰 추가)
        updateCourseRating(courseId, thisReviewRating, true);
        
        // 리뷰 상태를 승인으로 변경
        adminRepository.changeReviewApprove(findReview);
    }

    public BigDecimal calculateReviewRating(BigDecimal newTotalRating, int newTotalReviewCount) {
        if (newTotalReviewCount == 0) {
            return BigDecimal.ZERO;
        }
        return newTotalRating.divide(
                BigDecimal.valueOf(newTotalReviewCount), 1, RoundingMode.HALF_UP
        );
    }

    public void changeReviewReject(Long reviewId, String rejectionReason) {
        adminRepository.changeReviewReject(reviewId, rejectionReason);
    }

    public void updateCourseRating(UUID courseId, BigDecimal reviewRating, boolean isAdding) {
        BigDecimal currentTotalRatingSum = adminRepository.getTotalRatingByCourseId(courseId);
        int currentReviewCount = adminRepository.getTotalReviewCountByCourseId(courseId);
        
        BigDecimal newTotalRating;
        int newReviewCount;
        
        if (isAdding) {
            newTotalRating = currentTotalRatingSum.add(reviewRating);
            newReviewCount = currentReviewCount + 1;
        } else {
            newTotalRating = currentTotalRatingSum.subtract(reviewRating);
            newReviewCount = currentReviewCount - 1;
        }
        
        BigDecimal newAverageRating = calculateReviewRating(newTotalRating, newReviewCount);
        
        Course course = adminRepository.findCourseById(courseId);
        if (course != null) {
            course.setRating(newAverageRating);
            course.setComments(newReviewCount);
            course.setUpdated(true);
        }
    }
}
