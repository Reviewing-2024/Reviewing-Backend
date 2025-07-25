package com.reviewing.review.member.repository;

import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.domain.kakao.KakaoMemberInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member findMemberByKakaoId(Long kakaoId) {
        try {
            return em.createQuery("select m from Member m where m.kakaoId = :kakaoId", Member.class)
                    .setParameter("kakaoId", kakaoId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Member saveMemberByKakao(KakaoMemberInfoDto kakaoMemberInfo) {
        Member member = Member.builder()
                .kakaoId(kakaoMemberInfo.getKakaoId())
                .nickname(kakaoMemberInfo.getNickname())
                .build();
        em.persist(member);
        return member;
    }

}
