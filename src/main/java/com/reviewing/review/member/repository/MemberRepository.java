package com.reviewing.review.member.repository;

import com.reviewing.review.member.domain.KakaoMemberToken;
import com.reviewing.review.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findMemberById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public void saveKakaoToken(KakaoMemberToken kakaoMemberToken) {
        em.persist(kakaoMemberToken);
    }

    public Optional<KakaoMemberToken> findKakaoMemberTokenByMemberId(Long id) {
        try {
            KakaoMemberToken result = em.createQuery("select token from KakaoMemberToken token where token.memberId = :memberId", KakaoMemberToken.class)
                    .setParameter("memberId", id)
                    .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
