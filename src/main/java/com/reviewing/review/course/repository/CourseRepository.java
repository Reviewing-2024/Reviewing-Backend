package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Course;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;

    public List<Course> findAllCoursesBySorting() {
        return em.createQuery("select c from Course c",Course.class)
                .getResultList();
    }

}
