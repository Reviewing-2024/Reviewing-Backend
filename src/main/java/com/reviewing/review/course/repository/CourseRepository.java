package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Category;
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

    public List<CourseResponseDto> findAllCoursesBySorting(String sortType) {

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) "
                                    + "from Course c order by c.rating desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto" +
                                    "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url) "
                                    +
                                    "from Course c " +
                                    "left join Review r on c.id = r.course.id " +
                                    "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url "
                                    +
                                    "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c",
                        CourseResponseDto.class)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                    + "where c.platform = :platform "
                                    + "order by c.rating desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                    + "left join Review r on c.id = r.course.id " +
                                    "where c.platform = :platform " +
                                    "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url "
                                    +
                                    "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                + "where c.platform = :platform",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        Category findCategory = em.createQuery("select c from Category c where c.name = :name",
                        Category.class)
                .setParameter("name", category)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "order by c.rating desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("category", findCategory)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                    +
                                    "left join Review r on c.id = r.course.id " +
                                    "where c.platform = :platform and c.category = :category "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url "
                                    +
                                    "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("category", findCategory)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.rating,c.slug,c.url) from Course c "
                                + "where c.platform = :platform and c.category = :category",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .setParameter("category", findCategory)
                .getResultList();
    }
}
