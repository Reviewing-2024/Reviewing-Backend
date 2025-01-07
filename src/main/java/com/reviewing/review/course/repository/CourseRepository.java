package com.reviewing.review.course.repository;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.entity.CourseWish;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;

    private final int PAGE_SIZE = 10;

    // 모든 강의 조회
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType, UUID lastCourseId,
            BigDecimal lastRating, Integer lastComments) {

        // 평점순
        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "where (:lastRating is null or (c.rating < :lastRating or (c.rating = :lastRating and c.id < :lastCourseId))) "
                                    + "order by c.rating desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("lastRating", lastRating)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }
        // 댓글순
        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "where (:lastComments is null or (c.comments < :lastComments or (c.comments = :lastComments and c.id < :lastCourseId))) "
                                    + "order by c.comments desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("lastComments", lastComments)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }
        // 기본
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                + "from Course c "
                                + "where (:lastCourseId is null or c.id > :lastCourseId)"
                                + "order by c.id asc ",
                        CourseResponseDto.class)
                .setParameter("lastCourseId", lastCourseId)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
    }

    // 플랫폼 기준 정렬
    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType, UUID lastCourseId,
            BigDecimal lastRating, Integer lastComments) {

        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "where c.platform = :platform and (:lastRating is null or (c.rating < :lastRating or (c.rating = :lastRating and c.id < :lastCourseId))) "
                                    + "order by c.rating desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("lastRating", lastRating)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "where c.platform = :platform and (:lastComments is null or (c.comments < :lastComments or (c.comments = :lastComments and c.id < :lastCourseId)))"
                                    + "order by c.comments desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("lastComments", lastComments)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                + "from Course c "
                                + "where c.platform = :platform and (:lastCourseId is null or c.id > :lastCourseId)"
                                + "order by c.id asc ",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .setParameter("lastCourseId", lastCourseId)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
    }

    // 플랫폼,카테고리 기준 정렬 - 로그인 안함
    public List<CourseResponseDto> findCoursesByPlatformAndCategory(String platform,
            String category, String sortType, UUID lastCourseId,
            BigDecimal lastRating, Integer lastComments) {

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
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "join CategoryCourse cc on cc.course = c "
                                    + "where c.platform = :platform and cc.category = :category and (:lastRating is null or (c.rating < :lastRating or (c.rating = :lastRating and c.id < :lastCourseId))) "
                                    + "order by c.rating desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .setParameter("lastRating", lastRating)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                    + "from Course c "
                                    + "join CategoryCourse cc on cc.course = c "
                                    + "where c.platform = :platform and cc.category = :category and (:lastComments is null or (c.comments < :lastComments or (c.comments = :lastComments and c.id < :lastCourseId))) "
                                    + "order by c.comments desc, c.id desc ",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .setParameter("lastComments", lastComments)
                    .setParameter("lastCourseId", lastCourseId)
                    .setMaxResults(PAGE_SIZE)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                + "from Course c "
                                + "join CategoryCourse cc on cc.course = c "
                                + "where c.platform = :platform and cc.category = :category and (:lastCourseId is null or c.id > :lastCourseId)",
                        CourseResponseDto.class)
                .setParameter("platform", findPlatform)
                .setParameter("category", findCategory)
                .setParameter("lastCourseId", lastCourseId)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
    }

    public void createCourseWish(UUID courseId, Long memberId) {
        Course course = em.find(Course.class, courseId);
        Member member = em.find(Member.class, memberId);

        CourseWish courseWish = CourseWish.builder()
                .course(course)
                .member(member)
                .build();

        em.persist(courseWish);
    }

    public void removeCourseWish(UUID courseId, Long memberId) {
        CourseWish courseWish = em.createQuery(
                        "select cw from CourseWish cw where cw.course.id = :courseId and cw.member.id = :memberId",
                        CourseWish.class)
                .setParameter("courseId", courseId)
                .setParameter("memberId", memberId)
                .getSingleResult();

        em.remove(courseWish);
    }

    public void changeCourseUpdated(UUID courseId) {
        Course findCourse = em.find(Course.class, courseId);
        findCourse.setUpdated(true);
    }

    public CourseResponseDto findCourseById(UUID courseId) {
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes, c.comments) "
                                + "from Course c "
                                + "where c.id = :courseId ",
                        CourseResponseDto.class)
                .setParameter("courseId", courseId)
                .getSingleResult();
    }

    public List<Platform> findPlatforms() {
        return em.createQuery("select p from Platform p",Platform.class)
                .getResultList();
    }

    public List<CategoryResponseDto> findCategories(String platform) {
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CategoryResponseDto (c.name) from Category c "
                                + "where c.platform.name = :platform", CategoryResponseDto.class)
                .setParameter("platform", platform)
                .getResultList();
    }

    public CourseWish checkCourseWish(UUID courseId, Long memberId) {
        try {
            return em.createQuery(
                            "select cw from CourseWish cw where cw.course.id = :courseId and cw.member.id = :memberId",
                            CourseWish.class)
                    .setParameter("courseId", courseId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateCourseWishCount(UUID courseId, boolean wished) {
        Course findCourse = em.find(Course.class, courseId);
        int wishes = findCourse.getWishes();

        if (wished) {
            findCourse.setWishes(wishes + 1);
            return;
        }
        findCourse.setWishes(wishes - 1);
    }

    public CourseWish findCourseWish(UUID courseId, Long memberId) {
        try {
            return em.createQuery(
                            "select cw from CourseWish cw where cw.course.id = :courseId and cw.member.id = :memberId",
                            CourseWish.class)
                    .setParameter("courseId", courseId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}