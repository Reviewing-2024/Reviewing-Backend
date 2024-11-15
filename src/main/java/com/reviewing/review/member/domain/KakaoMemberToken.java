package com.reviewing.review.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KakaoMemberToken {

    @Id
    @GeneratedValue
    @Column(name = "kakao_member_token_id")
    private Long id;

    private Long memberId;
    private String kakaoAccessToken;
    private String kakaoRefreshToken;
}
