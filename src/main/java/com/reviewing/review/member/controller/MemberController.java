package com.reviewing.review.member.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.member.domain.KakaoTokenDto;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
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

//    @GetMapping("/test")
//    public Test test() {
//        Test test1 = new Test("test", 1);
//        return test1;
//    }

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

//         자체 refresh token
//        String refreshToken = jwtTokenProvider.createRefreshToken(member);

        // refresh token DB 저장
//        memberService.saveRefreshToken(member,refreshToken);

//        log.info("자체 refresh token={}",refreshToken);

        // localhost 테스트용 쿠키
//        ResponseCookie cookie = ResponseCookie.from("refresh-token",refreshToken)
//                .maxAge(14 * 24 * 60 * 60)
//                .path("/")
//                .httpOnly(true)
//                .build();

        /**
         * 배포용 쿠키
         */
//        ResponseCookie cookie = ResponseCookie.from("refresh-token",refreshToken)
//                .maxAge(14 * 24 * 60 * 60)
////                .maxAge(4 * 60) // 테스트용 4분
//                .path("/")
//                .sameSite("none") // 배포에서는 None
//                .secure(true)
//                .httpOnly(true)
//                .build();
//         만약 에러나면 domain 등록

        // 친구목록 테스트
//        KakaoFriendsDto kakaoFriendsDto = memberService.getFriends(kakaoAccessToken.getAccess_token());

        // 친구에게 메세지 보내기 테스트
//        memberService.sendMessage(kakaoAccessToken.getAccess_token());

        // 나에게 메세지 보내기 테스트
//        memberService.sendMessageToMe(kakaoAccessToken.getAccess_token());

        // 헤더에 jwt 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

//        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().headers(headers).body(member);
    }

}
