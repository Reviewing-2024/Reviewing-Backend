package com.reviewing.review.member.repository;

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
                        + "r.id, c.id, r.contents, rs.state, r.rating, "
                        + "count(rl.id), "
                        + "count(rd.id), "
                        + "rs.rejectionReason, "
                        + "r.createdAt) "
                        + "from Review r "
                        + "join r.member m "
                        + "join r.course c "
                        + "join r.reviewState rs "
                        + "left join ReviewLike rl on rl.review.id = r.id "
                        + "left join ReviewDislike rd on rd.review.id = r.id "
                        + "where r.member.id = :memberId ");

        if (status != null) {
            query.append("and rs.state = :status ");
        }

        query.append(
                "group by r.id, c.id, r.contents, rs.state,rs.rejectionReason, r.rating, r.createdAt");

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
}
