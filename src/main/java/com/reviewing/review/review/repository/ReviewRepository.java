package com.reviewing.review.review.repository;

import com.reviewing.review.course.entity.Course;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.review.entity.Review;
import com.reviewing.review.review.entity.ReviewDislike;
import com.reviewing.review.review.entity.ReviewLike;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.entity.ReviewState;
import com.reviewing.review.review.domain.ReviewStateByMemberDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewRepository {

    private final EntityManager em;
    private final ReviewRepositoryV2 reviewRepositoryV2;

    public Review createReview(UUID courseId, Long memberId, ReviewState reviewState, Review review) {

        Member findMember = em.find(Member.class, memberId);

        Course findCourse = em.find(Course.class, courseId);

        em.persist(reviewState);

        review.setMember(findMember);
        review.setCourse(findCourse);
        review.setReviewState(reviewState);

        em.persist(review);
        return review;
    }

    public List<ReviewResponseDto> findReviewsByCourse(UUID courseId) {

        return em.createQuery("select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, r.member.nickname, r.contents, r.rating, r.likes, r.dislikes, "
                                + "r.createdAt) "
                                + "from Review r "
                                + "where r.course.id = :courseId and r.reviewState.state = 'APPROVED' and r.isDeleted = false "
                                + "order by r.createdAt desc",
                        ReviewResponseDto.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public List<ReviewResponseDto> findReviewsWithLikedAndDislikedByCourse(UUID courseId) {

        return em.createQuery("select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, r.member.nickname, r.contents, r.rating, r.likes, r.dislikes, "
                                + "r.createdAt) "
                                + "from Review r "
                                + "where r.course.id = :courseId and r.reviewState.state = 'APPROVED' and r.isDeleted = false "
                                + "order by r.createdAt desc",
                        ReviewResponseDto.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public void createReviewLike(Long reviewId, Long memberId) {
        Review review = reviewRepositoryV2.findReviewByIdWithPessimisticLock(reviewId);
        Member member = em.find(Member.class, memberId);

        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .member(member)
                .build();
        em.persist(reviewLike);

        review.setLikes(review.getLikes() + 1);
    }
    public void removeReviewLike(Long reviewId, Long memberId) {
        Review review = reviewRepositoryV2.findReviewByIdWithPessimisticLock(reviewId);

        ReviewLike reviewLike = em.createQuery(
                        "select rl from ReviewLike rl where rl.review.id = :reviewId and rl.member.id = :memberId",
                        ReviewLike.class)
                .setParameter("reviewId", reviewId)
                .setParameter("memberId",memberId)
                .getSingleResult();

        em.remove(reviewLike);

        review.setLikes(review.getLikes() - 1);
    }

    public void createReviewDislike(Long reviewId, Long memberId) {
        Review review = reviewRepositoryV2.findReviewByIdWithPessimisticLock(reviewId);
        Member member = em.find(Member.class, memberId);

        ReviewDislike reviewDislike = ReviewDislike.builder()
                .review(review)
                .member(member)
                .build();

        em.persist(reviewDislike);

        review.setDislikes(review.getDislikes() + 1);
    }

    public void removeReviewDislike(Long reviewId, Long memberId) {
        Review review = reviewRepositoryV2.findReviewByIdWithPessimisticLock(reviewId);

        ReviewDislike reviewDislike = em.createQuery(
                        "select rd from ReviewDislike rd where rd.review.id = :reviewId and rd.member.id = :memberId",
                        ReviewDislike.class)
                .setParameter("reviewId", reviewId)
                .setParameter("memberId",memberId)
                .getSingleResult();

        em.remove(reviewDislike);

        review.setDislikes(review.getDislikes() - 1);
    }

    public ReviewResponseDto findReviewById(Long reviewId) {
        return em.createQuery(
                        "select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, r.member.nickname, r.contents, r.rating, r.likes, r.dislikes, "
                                + "r.createdAt) "
                                + "from Review r "
                                + "where r.id = :reviewId and r.reviewState.state = 'APPROVED' and r.isDeleted = false ",
                        ReviewResponseDto.class)
                .setParameter("reviewId", reviewId)
                .getSingleResult();
    }

    public ReviewLike checkReviewLiked(Long reviewId, Long memberId) {

        try {
            return em.createQuery(
                            "select rl from ReviewLike rl where rl.review.id = :reviewId and rl.member.id = :memberId",
                            ReviewLike.class)
                    .setParameter("reviewId", reviewId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public ReviewDislike checkReviewDisliked(Long reviewId, Long memberId) {

        try {
            return em.createQuery(
                            "select rd from ReviewDislike rd where rd.review.id = :reviewId and rd.member.id = :memberId",
                            ReviewDislike.class)
                    .setParameter("reviewId", reviewId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public ReviewStateByMemberDto findReviewByCourseIdAndMemberId(UUID courseId, Long memberId) {
        try {
            return em.createQuery(
                            "select new com.reviewing.review.review.domain.ReviewStateByMemberDto"
                                    + "(r.course.id, r.id, r.member.id, r.reviewState.state) "
                                    + "from Review r "
                                    + "where r.course.id=:courseId and r.member.id=:memberId and r.isDeleted = false",
                            ReviewStateByMemberDto.class)
                    .setParameter("courseId", courseId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void softDeleteReview(Long reviewId) {
        Review review = em.find(Review.class, reviewId);
        if (review != null) {
            review.setDeleted(true);
            review.setDeletedAt(LocalDateTime.now());
        }
    }

    public Review findReviewByReviewId(Long reviewId) {
        return em.find(Review.class, reviewId);
    }

    public boolean isReviewWrittenByMember(Long reviewId, Long memberId) {
        try {
            Review review = em.createQuery("select r from Review r "
                            + "where r.id = :reviewId and r.member.id = :memberId", Review.class)
                    .setParameter("reviewId", reviewId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
            return review != null;
        } catch (NoResultException e) {
            return false;
        }
    }
}
