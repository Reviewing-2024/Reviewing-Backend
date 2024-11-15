package com.reviewing.review.member.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.member.domain.KakaoTokenDto;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
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
     *
     */
    @GetMapping("/kakao/kakaoLogin/{code}")
    public ResponseEntity<Member> kakaoLogin(@PathVariable("code") String code, HttpServletResponse response) {

        log.info("인가코드={}",code);

        // kakao 토큰 받기
        // 토큰 받기 성공 -> 카카오에 계정 존재
        KakaoTokenDto kakaoTokenDto = memberService.getKakaoAccessToken(code);

        log.info("kakao accessToken={}", kakaoTokenDto.getAccess_token());

        // 유서 정보 받기
        Member member = memberService.getMemberInfo(kakaoTokenDto.getAccess_token());

        // 카카오 token db 저장
        memberService.saveKakaoToken(member, kakaoTokenDto);

//         자체 access token 생성
        String accessToken = jwtTokenProvider.createAccessToken(member);

        log.info("자체 access token={}",accessToken);

        // 헤더에 jwt 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return ResponseEntity.ok().headers(headers).body(member);
    }

}
