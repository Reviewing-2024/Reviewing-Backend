package com.reviewing.review.admin.repository;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.review.domain.ReviewStateType;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class AdminRepository {

    private final EntityManager em;

    public List<AdminReviewResponseDto> findReviewByStatus(ReviewStateType status) {

        return em.createQuery("select new com.reviewing.review.admin.domain.AdminReviewResponseDto "
                        + "(c.id,c.title,c.teacher,c.thumbnailImage,c.thumbnailVideo,c.url, "
                        + "r.id,r.contents,rs.state,r.certification) "
                        + "from Review r "
                        + "join r.course c "
                        + "join r.reviewState rs "
                        + "where rs.state = :status", AdminReviewResponseDto.class)
                .setParameter("status", status)
                .getResultList();

    }
}
