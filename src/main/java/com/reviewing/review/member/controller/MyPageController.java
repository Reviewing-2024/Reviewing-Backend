package com.reviewing.review.member.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.domain.MemberNicknameDto;
import com.reviewing.review.member.domain.MyReviewResponseDto;
import com.reviewing.review.member.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/my")
public class MyPageController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MyPageService myPageService;

    @GetMapping("/reviews")
    public ResponseEntity<List<MyReviewResponseDto>> findMyReviewsByStatus(
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        List<MyReviewResponseDto> myReviews = myPageService.findMyReviewsByStatus(status, memberId);

        return ResponseEntity.ok().body(myReviews);
    }

    @GetMapping("/wish/courses")
    public ResponseEntity<List<CourseResponseDto>> findWishCourseByMember(
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        List<CourseResponseDto> courses = myPageService.findWishCourseByMember(memberId);

        return ResponseEntity.ok().body(courses);
    }

    @PutMapping("/nickname")
    public ResponseEntity<Member> updateUserNickname(@RequestBody MemberNicknameDto memberNicknameDto,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        myPageService.updateUserNickname(memberId, memberNicknameDto.getNickName());

        Member member = myPageService.findMemberById(memberId);

        String accessToken = jwtTokenProvider.createAccessToken(member);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        return ResponseEntity.ok().headers(headers).body(member);
    }

}
