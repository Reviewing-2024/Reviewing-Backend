package com.reviewing.review.review.repository;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewDislike;
import com.reviewing.review.review.domain.ReviewLike;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.domain.ReviewState;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewRepository {

    private final EntityManager em;

    public void createReview(Long courseId, Long memberId, ReviewState reviewState, Review review) {

        Member findMember = em.find(Member.class, memberId);

        Course findCourse = em.find(Course.class, courseId);

        em.persist(reviewState);

        review.setMember(findMember);
        review.setCourse(findCourse);
        review.setReviewState(reviewState);

        em.persist(review);
    }

    public List<ReviewResponseDto> findReviewsByCourse(Long courseId) {

        return em.createQuery("select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, m.nickname, r.contents, r.rating, "
                                + "count(rl.id), "
                                + "count(rd.id), "
                                + "r.createdAt) "
                                + "from Review r "
                                + "join r.member m "
                                + "join r.course c "
                                + "join r.reviewState rs "
                                + "left join ReviewLike rl on rl.review.id = r.id "
                                + "left join ReviewDislike rd on rd.review.id = r.id "
                                + "where c.id = :courseId and rs.state = 'APPROVED' "
                                + "group by r.id, m.nickname, r.contents, r.rating, r.createdAt",
                        ReviewResponseDto.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public List<ReviewResponseDto> findReviewsWithLikedAndDislikedByCourse(Long courseId,
            Long memberId) {

        return em.createQuery("select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, m.nickname, r.contents, r.rating, "
                                + "count(rl.id), "
                                + "count(rd.id), "
                                + "(case when rl.member.id = :memberId then true else false end), "
                                + "(case when rd.member.id = :memberId then true else false end), "
                                + "r.createdAt) "
                                + "from Review r "
                                + "join r.member m "
                                + "join r.course c "
                                + "join r.reviewState rs "
                                + "left join ReviewLike rl on rl.review.id = r.id "
                                + "left join ReviewDislike rd on rd.review.id = r.id "
                                + "where c.id = :courseId and rs.state = 'APPROVED' "
                                + "group by r.id, m.nickname, r.contents, r.rating, r.createdAt, rl.member.id, rd.member.id",
                        ReviewResponseDto.class)
                .setParameter("courseId", courseId)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void createReviewLike(Long reviewId, Long memberId) {
        Review review = em.find(Review.class, reviewId);
        Member member = em.find(Member.class, memberId);

        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .member(member)
                .build();

        em.persist(reviewLike);
    }

    public void removeReviewLike(Long reviewId, Long memberId) {

        ReviewLike reviewLike = em.createQuery(
                        "select rl from ReviewLike rl where rl.review.id = :reviewId and rl.member.id = :memberId",
                        ReviewLike.class)
                .setParameter("reviewId", reviewId)
                .setParameter("memberId",memberId)
                .getSingleResult();

        em.remove(reviewLike);
    }

    public void createReviewDislike(Long reviewId, Long memberId) {
        Review review = em.find(Review.class, reviewId);
        Member member = em.find(Member.class, memberId);

        ReviewDislike reviewDislike = ReviewDislike.builder()
                .review(review)
                .member(member)
                .build();

        em.persist(reviewDislike);
    }

    public void removeReviewDislike(Long reviewId, Long memberId) {

        ReviewDislike reviewDislike = em.createQuery(
                        "select rd from ReviewDislike rd where rd.review.id = :reviewId and rd.member.id = :memberId",
                        ReviewDislike.class)
                .setParameter("reviewId", reviewId)
                .setParameter("memberId",memberId)
                .getSingleResult();

        em.remove(reviewDislike);
    }

    public ReviewResponseDto findReviewById(Long reviewId, Long memberId) {
        return em.createQuery(
                        "select new com.reviewing.review.review.domain.ReviewResponseDto( "
                                + "r.id, m.nickname, r.contents, r.rating, "
                                + "count(rl.id), "
                                + "count(rd.id), "
                                + "(case when rl.member.id = :memberId then true else false end), "
                                + "(case when rd.member.id = :memberId then true else false end), "
                                + "r.createdAt) "
                                + "from Review r "
                                + "join r.member m "
                                + "join r.course c "
                                + "join r.reviewState rs "
                                + "left join ReviewLike rl on rl.review.id = r.id "
                                + "left join ReviewDislike rd on rd.review.id = r.id "
                                + "where r.id = :reviewId and rs.state = 'APPROVED' "
                                + "group by r.id, m.nickname, r.contents, r.rating, r.createdAt, rl.member.id, rd.member.id",
                        ReviewResponseDto.class)
                .setParameter("reviewId", reviewId)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }
}
