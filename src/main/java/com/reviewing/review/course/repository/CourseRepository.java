package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Category;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.Platform;
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

    public List<CourseResponseDto> findAllCoursesBySorting() {
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c",
                        CourseResponseDto.class)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                + "where c.platform = :platform",
                        CourseResponseDto.class)
                .setParameter("platform",finePlatform)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        Category findCategory = em.createQuery("select c from Category c where c.name = :name",
                        Category.class)
                .setParameter("name", category)
                .getSingleResult();

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                + "where c.platform = :platform and c.category = :category",
                        CourseResponseDto.class)
                .setParameter("platform",finePlatform)
                .setParameter("category",findCategory)
                .getResultList();
    }
}
