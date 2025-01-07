package com.reviewing.review.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private Long kakaoId;
    private String nickname;

    @Builder
    public Member(Long kakaoId, String nickname) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
    }

}

