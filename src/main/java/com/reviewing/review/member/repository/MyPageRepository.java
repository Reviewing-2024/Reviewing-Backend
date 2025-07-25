package com.reviewing.review.member.repository;

import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.domain.MyReviewResponseDto;
import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {

    private final EntityManager em;

    public List<MyReviewResponseDto> findMyReviewsByStatus(ReviewStateType status, Long memberId) {

        StringBuilder query = new StringBuilder(
                "select new com.reviewing.review.member.domain.MyReviewResponseDto( "
                        + "r.id, r.course.id, r.course.title, r.course.slug, r.contents, r.reviewState.state, r.rating, r.likes, r.dislikes, "
                        + "r.reviewState.rejectionReason, "
                        + "r.createdAt) "
                        + "from Review r "
                        + "where r.member.id = :memberId ");

        if (status != null) {
            query.append("and r.reviewState.state = :status ");
        }
        query.append("order by r.createdAt desc");
        TypedQuery<MyReviewResponseDto> queryResult = em.createQuery(query.toString(), MyReviewResponseDto.class)
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
                                + "c.wishes, c.comments ,true) "
                                + "from Course c "
                                + "join CourseWish w on w.course.id = c.id "
                                + "where w.member.id = :memberId ",
                        CourseResponseDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

}
