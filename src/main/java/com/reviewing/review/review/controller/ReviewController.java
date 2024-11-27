package com.reviewing.review.review.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewService reviewService;

    @PostMapping("/{courseId}")
    public void createReview(@PathVariable Long courseId, ReviewRequestDto reviewRequestDto,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        reviewService.createReview(courseId, memberId, reviewRequestDto);
    }

    // 테스트용
//    @PostMapping("/{courseId}")
//    public void createReview(@PathVariable Long courseId,@RequestBody ReviewRequestDto reviewRequestDto) {
//
//        Long memberId = ;
//
//        reviewService.createReview(courseId, memberId, reviewRequestDto);
//    }

}
