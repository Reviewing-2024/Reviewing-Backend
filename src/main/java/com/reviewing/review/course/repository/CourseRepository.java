package com.reviewing.review.course.repository;

import com.reviewing.review.course.domain.Category;
import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.Course;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.CourseWish;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.member.domain.Member;
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

    // 모든 강의 조회 - 로그인 안함
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType) {

        // 평점순
        if (sortType != null && sortType.equals("rating")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                    + "count(w.id)) "
                                    + "from Course c "
                                    + "left join CourseWish w on w.course.id = c.id "
                                    + "group by c.id "
                                    + "order by c.rating desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        // 댓글순
        if (sortType != null && sortType.equals("comments")) {
            return em.createQuery(
                            "select new com.reviewing.review.course.domain.CourseResponseDto"
                                    + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url) "
                                    + "from Course c "
                                    + "left join Review r on c.id = r.course.id "
//                                    + "left join CourseWish w on w.course.id = c.id " +
                                    + "group by c.id "
                                    + "order by count(r.id) desc",
                            CourseResponseDto.class)
                    .getResultList();
        }

        // 기본
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto"
                                + "(c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id)) "
                                + "from Course c "
                                + "left join CourseWish w on w.course.id = c.id "
                                + "group by c.id",
                        CourseResponseDto.class)
                .getResultList();
    }

    // 모든 강의 조회 - 로그인 함
    public List<CourseResponseDto> findAllCoursesBySorting(String sortType, Long memberId) {

        // 평점순
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

        // 댓글순
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

        // 기본순
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

    // 플랫폼 기준 정렬 - 로그인 안함
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
                                    + "left join r.reviewState rs "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url "
                                    + "order by count(case when rs.state = 'APPROVED' then 1 else null end) desc",
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

    // 플랫폼 기준 정렬 - 로그인 함
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
                                    + "left join r.reviewState rs "
                                    + "where c.platform = :platform "
                                    + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                    + "c.rating, c.slug, c.url, w.member.id "
                                    + "order by count(case when rs.state = 'APPROVED' then 1 else null end) desc",
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

    // 플랫폼,카테고리 기준 정렬 - 로그인 함
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
//                                + "count(w.id)) "
                                + "from Course c "
//                                + "left join CourseWish w on w.course.id = c.id "
                                + "where c.id = :courseId "
//                                + "group by c.id"
                        ,
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

    public long findCourseWishes(Long courseId) {
        return em.createQuery("select cw from CourseWish cw where cw.course.id = :courseId",
                        CourseWish.class)
                .setParameter("courseId", courseId)
                .getResultList()
                .size();
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
}