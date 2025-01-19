package com.reviewing.review.recommend.repository;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.recommend.domain.RecommendResponseDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
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
    public Course findCourseById(UUID courseId) {
        return em.find(Course.class, courseId);
    }

    public RecommendResponseDto findCourseBySearchResponses(UUID id) {
        return em.createQuery(
                        "select new com.reviewing.review.recommend.domain.RecommendResponseDto(c.id ,c.title, c.teacher, c.url, c.slug) " +
                                "from Course c where c.id = :id", RecommendResponseDto.class)
                .setParameter("id", id) // 파라미터 바인딩
                .getSingleResult(); // 결과 반환
    }

    public List<Category> findCategoryByCourseId(UUID courseId) {

        return em.createQuery(
                        "select category from Category category "
                                + "join CategoryCourse cc on cc.category = category "
                                + "where cc.course.id = :courseId ", Category.class)
                .setParameter("courseId",courseId)
                .getResultList();
    }
}
