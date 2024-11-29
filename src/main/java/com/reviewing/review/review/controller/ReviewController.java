package com.reviewing.review.review.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.service.ReviewService;
import com.reviewing.review.review.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewService reviewService;
    private final S3Service s3Service;

//    @PostMapping(value = "/{courseId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public void createReview(@PathVariable Long courseId,
//            @RequestPart ReviewRequestDto reviewRequestDto,
//            @RequestPart MultipartFile certificationFile,
//            HttpServletRequest request) throws IOException {
//
//        String jwtHeader = request.getHeader("Authorization");
//        String token = jwtHeader.replace("Bearer ", "");
//        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
//
//        String certification = s3Service.saveFile(certificationFile);
//
//        reviewService.createReview(courseId, memberId, reviewRequestDto, certification);
//    }

    // 테스트용
    @PostMapping(value = "/{courseId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void createReview(@PathVariable Long courseId,
            @RequestPart ReviewRequestDto reviewRequestDto,
            @RequestPart MultipartFile certificationFile) throws IOException {

        Long memberId = 3783566065L;

        String certification = s3Service.saveFile(certificationFile);

        reviewService.createReview(courseId, memberId, reviewRequestDto, certification);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<List<ReviewResponseDto>> findReviewsByCourse(
            @PathVariable Long courseId) {

        List<ReviewResponseDto> reviews = reviewService.findReviewsByCourse(courseId);

        return ResponseEntity.ok().body(reviews);
    }

    @PostMapping("/{reviewId}/like")
    public void createReviewLike(@PathVariable Long reviewId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        reviewService.createReviewLike(reviewId, memberId);
    }

    @DeleteMapping("/{reviewId}/like")
    public void removeReviewLike(@PathVariable Long reviewId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        reviewService.removeReviewLike(reviewId, memberId);
    }

    @PostMapping("/{reviewId}/dislike")
    public void createReviewDislike(@PathVariable Long reviewId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        reviewService.createReviewDislike(reviewId, memberId);
    }

    @DeleteMapping("/{reviewId}/dislike")
    public void removeReviewDislike(@PathVariable Long reviewId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        reviewService.removeReviewDislike(reviewId, memberId);
    }

}
