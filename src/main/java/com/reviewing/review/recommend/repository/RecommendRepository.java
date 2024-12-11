package com.reviewing.review.recommend.repository;

import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.recommend.domain.RecommendResponseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendRepository {

    private final EntityManager em;

    public Course findCourseById(Long i) {

        return em.find(Course.class, i);
    }

    public RecommendResponseDto findCourseBySearchResponses(Long id) {
        return em.createQuery(
                        "select new com.reviewing.review.recommend.domain.RecommendResponseDto(c.title, c.teacher, c.url) " +
                                "from Course c where c.id = :id", RecommendResponseDto.class)
                .setParameter("id", id) // 파라미터 바인딩
                .getSingleResult(); // 결과 반환
    }

}
