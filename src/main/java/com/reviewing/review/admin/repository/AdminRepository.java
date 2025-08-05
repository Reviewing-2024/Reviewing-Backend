package com.reviewing.review.admin.repository;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.review.entity.Review;
import com.reviewing.review.review.domain.ReviewStateType;
import com.reviewing.review.course.entity.Course;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final EntityManager em;

    public List<AdminReviewResponseDto> findReviewByStatus(ReviewStateType status) {

        return em.createQuery("select new com.reviewing.review.admin.domain.AdminReviewResponseDto "
                        + "(r.course.id, r.course.title, r.course.teacher, r.course.thumbnailImage, r.course.thumbnailVideo,r.course.url, "
                        + "r.id,r.contents, r.reviewState.state,r.certification,r.reviewState.updatedAt) "
                        + "from Review r "
                        + "where r.reviewState.state = :status and r.isDeleted = false order by r.id desc ", AdminReviewResponseDto.class)
                .setParameter("status", status)
                .getResultList();

    }

    public Review findReviewById(Long reviewId) {
        return em.find(Review.class, reviewId);
    }

    public void changeReviewApprove(Review review) {
        review.getReviewState().setState(ReviewStateType.APPROVED);
        review.getReviewState().setUpdatedAt(LocalDateTime.now());
    }

    public void changeReviewReject(Long reviewId, String rejectionReason) {

        Review fineReview = em.find(Review.class, reviewId);

        fineReview.getReviewState().setState(ReviewStateType.REJECTED);
        fineReview.getReviewState().setRejectionReason(rejectionReason);
        fineReview.getReviewState().setUpdatedAt(LocalDateTime.now());
    }

    public int getTotalReviewCountByCourseId(UUID courseId) {

        return em.createQuery("select r "
                        + "from Review r "
                        + "where r.reviewState.state = 'APPROVED' "
                        + "and r.course.id = :courseId and r.isDeleted = false", Review.class)
                .setParameter("courseId", courseId)
                .getResultList()
                .size();
    }

    public BigDecimal getTotalRatingByCourseId(UUID courseId) {
        try {
            return em.createQuery("select sum(r.rating) "
                            + "from Review r "
                            + "where r.course.id = :courseId and r.reviewState.state = 'APPROVED' and r.isDeleted = false", BigDecimal.class)
                    .setParameter("courseId", courseId)
                    .getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Course findCourseById(UUID courseId) {
        return em.find(Course.class, courseId);
    }

}
