package com.reviewing.review.member.repository;

import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.member.domain.MyReviewResponseDto;
import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class MyPageRepository {

    private final EntityManager em;

    public List<MyReviewResponseDto> findMyReviewsByStatus(ReviewStateType status, Long memberId) {

        StringBuilder query = new StringBuilder(
                "select new com.reviewing.review.member.domain.MyReviewResponseDto( "
                        + "r.id, r.course.id, r.contents, r.reviewState.state, r.rating, "
                        + "count(rl.id), "
                        + "r.reviewState.rejectionReason, "
                        + "r.createdAt) "
                        + "from Review r "
                        + "left join ReviewLike rl on rl.review.id = r.id "
                        + "where r.member.id = :memberId ");

        if (status != null) {
            query.append("and r.reviewState.state = :status ");
        }

        query.append(
                "group by r.id, r.reviewState.state, r.reviewState.rejectionReason");

        // 쿼리 실행
        var queryResult = em.createQuery(query.toString(), MyReviewResponseDto.class)
                .setParameter("memberId", memberId);

        if (status != null) {
            queryResult.setParameter("status", status);
        }

        return queryResult.getResultList();
    }


    public void updateUserNickname(Long memberId, String nickName) {
        Member findMember = em.find(Member.class, memberId);
        findMember.setNickname(nickName);
    }

    public Member findMemberById(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public List<CourseResponseDto> findWishCourseByMember(Long memberId) {
        return em.createQuery(
                        "select new com.reviewing.review.course.domain.CourseResponseDto("
                                + "c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, c.rating, c.slug, c.url, "
                                + "count(w.id), true) "
                                + "from Course c "
                                + "join CourseWish w on w.course.id = c.id "
                                + "where w.member.id = :memberId "
                                + "group by c.id, c.title, c.teacher, c.thumbnailImage, c.thumbnailVideo, "
                                + "c.rating, c.slug, c.url",
                        CourseResponseDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
