package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Category;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.CourseWish;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.member.domain.Member;
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
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id " +
                                    "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url "
                                    +
                                    "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id)) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url ",
                        CourseResponseDto.class)
                .getResultList();
    }

    public List<CourseResponseDto> findAllCoursesBySorting(String sortType, Long memberId) {

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id) , "
                                    + "case when w.member.id = :memberId then true else false end ) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {

            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id), "
                                    + "case when w.member.id = :memberId then true else false end) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id), "
                                + "case when w.member.id = :memberId then true else false end) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url, w.member.id",
                        CourseResponseDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id)) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.platform = :platform "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType,
            Long memberId) {

        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id), "
                                    + "case when w.member.id = :memberId then true else false end) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id), "
                                    + "case when w.member.id = :memberId then true else false end) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id), "
                                + "case when w.member.id = :memberId then true else false end) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.platform = :platform "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url, w.member.id",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType) {
        Platform findPlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        Category findCategory = em.createQuery(
                        "select c from Category c where c.name = :name and c.platform.id = :platformId",
                        Category.class)
                .setParameter("name", category)
                .setParameter("platformId", findPlatform.getId()) // 플랫폼 조건 추가
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id)) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.platform = :platform and c.category = :category "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url",
                        CourseResponseDto.class)
                .setParameter("platform", findPlatform)
                .setParameter("category", findCategory)
                .getResultList();
    }

    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType, Long memberId) {

        Platform findPlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        Category findCategory = em.createQuery(
                        "select c from Category c where c.name = :name and c.platform.id = :platformId",
                        Category.class)
                .setParameter("name", category)
                .setParameter("platformId", findPlatform.getId()) // 플랫폼 조건 추가
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id), "
                                    + "case when w.member.id = :memberId then true else false end) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id), "
                                    + "case when w.member.id = :memberId then true else false end) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .setParameter("memberId", memberId)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id), "
                                + "case when w.member.id = :memberId then true else false end) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.platform = :platform and c.category = :category "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url, w.member.id",
                        CourseResponseDto.class)
                .setParameter("platform", findPlatform)
                .setParameter("category", findCategory)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void createCourseWish(Long courseId, Long memberId) {
        Course course = em.find(Course.class, courseId);
        Member member = em.find(Member.class, memberId);

        CourseWish courseWish = CourseWish.builder()
                .course(course)
                .member(member)
                .build();

        em.persist(courseWish);
    }

    public void removeCourseWish(Long courseId, Long memberId) {
        CourseWish courseWish = em.createQuery("select cw from CourseWish cw where cw.course.id = :courseId and cw.member.id = :memberId",CourseWish.class)
                .setParameter("courseId",courseId)
                .setParameter("memberId",memberId)
                .getSingleResult();

        em.remove(courseWish);
    }

    public void changeCourseUpdated(Long courseId) {
        Course findCourse = em.find(Course.class, courseId);
        findCourse.setUpdated(true);
    }

    public CourseResponseDto findCourseById(Long courseId, Long memberId) {
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id), "
                                + "case when w.member.id = :memberId then true else false end) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.id = :courseId "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url, w.member.id",
                        CourseResponseDto.class)
                .setParameter("courseId", courseId)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }
}
