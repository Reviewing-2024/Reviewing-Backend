package com.reviewing.review.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member {

    @Id
    @Column(name = "id")
    private Long memberId;

    private String nickname;

    @Builder
    public Member(String nickname) {
        this.nickname = nickname;
    }

}

