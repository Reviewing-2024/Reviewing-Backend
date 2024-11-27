package com.reviewing.review.review.repository;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.review.domain.Review;
import com.reviewing.review.review.domain.ReviewState;
import jakarta.persistence.EntityManager;
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
}
