package com.reviewing.review.member.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.member.domain.kakao.KakaoMemberInfoDto;
import com.reviewing.review.member.domain.kakao.KakaoTokenDto;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인
     * 로컬 테스트일 때만 Get 요청으로 변경해둠
     */
    @PostMapping("/kakao/kakaoLogin/{code}")
    public ResponseEntity<Member> kakaoLogin(@PathVariable("code") String code) {
        // 토큰 받기 성공 -> 카카오에 계정 존재
        KakaoTokenDto kakaoTokenDto = memberService.getKakaoAccessToken(code);
        // 카카오 유저 정보
        KakaoMemberInfoDto kakaoMemberInfo = memberService.getKakaoMemberInfo(kakaoTokenDto.getAccess_token());
        // 회원가입 or 로그인
        Member member = memberService.signupOrLoginByKakaoId(kakaoMemberInfo);
        String accessToken = jwtTokenProvider.createAccessToken(member);
        log.info("자체 access token={}",accessToken);

        // 헤더에 jwt 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return ResponseEntity.ok().headers(headers).body(member);
    }

}
