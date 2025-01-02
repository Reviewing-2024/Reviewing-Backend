package com.reviewing.review.admin.service;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.admin.domain.RejectionDto;
import com.reviewing.review.admin.repository.AdminRepository;
import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewStateType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

        Review findReview = adminRepository.findReviewById(reviewId);

        float thisReviewRating = findReview.getRating();
        float courseRating = findReview.getCourse().getRating();

        int totalReviewCount = adminRepository.getTotalReviewCountByReviewId(findReview.getCourse().getId());

        float newTotalRating = thisReviewRating + courseRating;
        int newTotalReviewCount = totalReviewCount + 1;

        adminRepository.updateReviewRating(findReview,
                calculateReviewRating(newTotalRating, newTotalReviewCount));

        adminRepository.changeReviewApprove(findReview);

        adminRepository.changeCourseUpdated(reviewId);
    }

    public float calculateReviewRating(float newTotalRating, int newTotalReviewCount) {
        return Math.round(newTotalRating / newTotalReviewCount * 10) / 10.0f;
    }

    public void changeReviewReject(Long reviewId, String rejectionReason) {
        adminRepository.changeReviewReject(reviewId, rejectionReason);
    }
}
