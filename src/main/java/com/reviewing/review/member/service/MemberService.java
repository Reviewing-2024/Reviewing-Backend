package com.reviewing.review.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reviewing.review.member.domain.KakaoMemberToken;
import com.reviewing.review.member.domain.KakaoTokenDto;
import com.reviewing.review.member.domain.KakaoUserInfoDto;
import com.reviewing.review.member.domain.Member;
import com.reviewing.review.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@RequiredArgsConstructor
@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Value("${Kakao.clientId}")
    private String clientId;

    public KakaoTokenDto getKakaoAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:3000/login/callback/kakao");
        params.add("code", code);
//        params.add("client_secret", KAKAO_CLIENT_SECRET); 선택 사항

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    public Member getMemberInfo(String kakaoAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청 후 response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoUserInfoDto kakaoUerInfoDto = null;
        try {
            kakaoUerInfoDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoUserInfoDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Member member = new Member(kakaoUerInfoDto.getId()
                ,kakaoUerInfoDto.getKakao_account().getProfile().getNickname());

        log.info("카카오에서 가져온 id={}",kakaoUerInfoDto.getId());
        log.info("카카오에서 가져온 nickname={}",kakaoUerInfoDto.getKakao_account().getProfile().getNickname());

        Optional<Member> existOwner = memberRepository.findMemberById(member.getMemberId());

        if (existOwner.isEmpty()) {
            // 회원가입
            memberRepository.save(member);
        }
        return member;

    }

    public void saveKakaoToken(Member member, KakaoTokenDto kakaoTokenDto) {

        KakaoMemberToken kakaoMemberToken = new KakaoMemberToken();
        kakaoMemberToken.setMemberId(member.getMemberId());
        kakaoMemberToken.setKakaoAccessToken(kakaoTokenDto.getAccess_token());
        kakaoMemberToken.setKakaoRefreshToken(kakaoTokenDto.getRefresh_token());

        KakaoMemberToken dbKakaoMemberToken = getDbKakaoMemberToken(member.getMemberId());

        if (dbKakaoMemberToken != null) {
            dbKakaoMemberToken.setKakaoAccessToken(kakaoTokenDto.getAccess_token());
            dbKakaoMemberToken.setKakaoRefreshToken(kakaoTokenDto.getRefresh_token());
        } else {
            memberRepository.saveKakaoToken(kakaoMemberToken);

        }
    }

    public KakaoMemberToken getDbKakaoMemberToken(Long memberId) {
        Optional<KakaoMemberToken> kakaoMemberToken = memberRepository.findKakaoMemberTokenByMemberId(memberId);

        if (kakaoMemberToken.isPresent()) {
            return kakaoMemberToken.get();
        } else {
            return null;
        }
    }


}
