package com.reviewing.review.admin.repository;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class AdminRepository {

    private final EntityManager em;

    public List<AdminReviewResponseDto> findReviewByStatus(ReviewStateType status) {

        return em.createQuery("select new com.reviewing.review.admin.domain.AdminReviewResponseDto "
                        + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.url, "
                        + "r.id,r.contents,rs.state,r.certification) "
                        + "from Review r "
                        + "join r.course c "
                        + "join r.reviewState rs "
                        + "where rs.state = :status", AdminReviewResponseDto.class)
                .setParameter("status", status)
                .getResultList();

    }

    public Review findReviewById(Long reviewId) {
        return em.find(Review.class, reviewId);
    }

    public void changeReviewApprove(Review review) {
        review.getReviewState().setState(ReviewStateType.APPROVED);
    }

    public void changeReviewReject(Long reviewId, String rejectionReason) {

        Review fineReview = em.find(Review.class, reviewId);

        fineReview.getReviewState().setState(ReviewStateType.REJECTED);
        fineReview.getReviewState().setRejectionReason(rejectionReason);
    }

    public void updateReviewRating(Review review, float newReviewRating) {
        review.getCourse().setRating(newReviewRating);
    }

    public void changeCourseUpdated(Long reviewId) {
        Review findReview = em.find(Review.class, reviewId);
        findReview.getCourse().setUpdated(true);
    }

    public int getTotalReviewCountByReviewId(Long courseId) {

        return em.createQuery("select r "
                        + "from Review r "
                        + "join r.course c "
                        + "join r.reviewState rs "
                        + "where rs.state = 'APPROVED' "
                        + "and c.id = :courseId", Review.class)
                .setParameter("courseId", courseId)
                .getResultList()
                .size();

    }
}
