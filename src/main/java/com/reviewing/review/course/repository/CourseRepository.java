package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Category;
import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.CourseWish;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;

    // 모든 강의 조회
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType) {
        // 평점순
        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .getResultList();
        }
        // 댓글순
        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "group by c.id "
                                    + "order by count(case when r.reviewState.state = :reviewStateType then 1 else null end) desc",
                            CourseResponseDto.class)
                    .setParameter("reviewStateType", ReviewStateType.APPROVED)
                    .getResultList();
        }
        // 기본
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                + "from Course c ",
                        CourseResponseDto.class)
                .getResultList();
    }

    // 플랫폼 기준 정렬
    public List<CourseResponseDto> findCoursesByPlatform(String platform, String sortType) {
        Platform finePlatform = em.createQuery("select p from Platform p where p.name = :name",
                        Platform.class)
                .setParameter("name", platform)
                .getSingleResult();

        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "where c.platform = :platform "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "and (r.reviewState.state = :reviewStateType) "
                                    + "where c.platform = :platform "
                                    + "group by c.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", finePlatform)
                    .setParameter("reviewStateType", ReviewStateType.APPROVED)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                + "from Course c "
                                + "where c.platform = :platform ",
                        CourseResponseDto.class)
                .setParameter("platform", finePlatform)
                .getResultList();
    }

    // 플랫폼,카테고리 기준 정렬 - 로그인 안함
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
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .getResultList();
        }

        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto("
                                    + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
                                    + "and (r.reviewState.state = :reviewStateType) "
                                    + "where c.platform = :platform and c.category = :category "
                                    + "group by c.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .setParameter("platform", findPlatform)
                    .setParameter("category", findCategory)
                    .setParameter("reviewStateType", ReviewStateType.APPROVED)
                    .getResultList();
        }

        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
                                + "from Course c "
                                + "where c.platform = :platform and c.category = :category ",
                        CourseResponseDto.class)
                .setParameter("platform", findPlatform)
                .setParameter("category", findCategory)
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
        CourseWish courseWish = em.createQuery(
                        "select cw from CourseWish cw where cw.course.id = :courseId and cw.member.id = :memberId",
                        CourseWish.class)
                .setParameter("courseId", courseId)
                .setParameter("memberId", memberId)
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
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, c.wishes) "
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

    public CourseWish checkCourseWish(Long courseId, Long memberId) {
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

    public void updateCourseWishCount(Long courseId, boolean wished) {
        Course findCourse = em.find(Course.class, courseId);
        int wishes = findCourse.getWishes();

        if (wished) {
            findCourse.setWishes(wishes + 1);
            return;
        }
        findCourse.setWishes(wishes - 1);
    }

    public CourseWish findCourseWish(Long courseId, Long memberId) {
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