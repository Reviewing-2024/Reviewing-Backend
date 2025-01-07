package com.reviewing.review.review.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.review.domain.ReviewRequestDto;
import com.reviewing.review.review.domain.ReviewResponseDto;
import com.reviewing.review.review.repository.ReviewRepository;
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

    @PostMapping(value = "/{courseId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createReview(@PathVariable Long courseId,
            @RequestPart ReviewRequestDto reviewRequestDto,
            @RequestPart MultipartFile certificationFile,
            HttpServletRequest request) throws IOException {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        String certification = s3Service.saveFile(certificationFile, memberId, courseId);

        reviewService.createReview(courseId, memberId, reviewRequestDto, certification);
        return ResponseEntity.ok().body("리뷰 작성 성공");
    }

    // 테스트용
//    @PostMapping(value = "/{courseId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public void createReview(@PathVariable Long courseId,
//            @RequestPart ReviewRequestDto reviewRequestDto,
//            @RequestPart MultipartFile certificationFile) throws IOException {
//
//        Long memberId = ;
//
//        String certification = s3Service.saveFile(certificationFile, memberId, courseId);
//
//        reviewService.createReview(courseId, memberId, reviewRequestDto, certification);
//    }

    @GetMapping("/{courseId}")
    public ResponseEntity<List<ReviewResponseDto>> findReviewsByCourse(
            @PathVariable Long courseId, HttpServletRequest request) {

        List<ReviewResponseDto> reviews;

        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            String token = jwtHeader.replace("Bearer ", "");
            Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
            if (memberId == null) {
                return ResponseEntity.status(600).body(null);
            }
            reviews = reviewService.findReviewsWithLikedAndDislikedByCourse(courseId, memberId);

            return ResponseEntity.ok().body(reviews);
        }

        reviews = reviewService.findReviewsByCourse(courseId);

        return ResponseEntity.ok().body(reviews);
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewResponseDto> createReviewLike(@PathVariable Long reviewId,
            @RequestParam(value = "liked") boolean liked,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        if (liked) { // liked = true 일 때 -> 좋아요 취소
            reviewService.removeReviewLike(reviewId, memberId);
            ReviewResponseDto reviewResponseDto =  reviewService.findReviewById(reviewId, memberId);
            return ResponseEntity.ok().body(reviewResponseDto);
        }

        // liked = false 일 때 -> 좋아요
        reviewService.createReviewLike(reviewId, memberId);
        ReviewResponseDto reviewResponseDto =  reviewService.findReviewById(reviewId, memberId);
        return ResponseEntity.ok().body(reviewResponseDto);
    }

    @PostMapping("/{reviewId}/dislike")
    public ResponseEntity<ReviewResponseDto> createReviewDislike(@PathVariable Long reviewId,
            @RequestParam(value = "disliked") boolean disliked,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        if (disliked) {
            reviewService.removeReviewDislike(reviewId, memberId);
            ReviewResponseDto reviewResponseDto =  reviewService.findReviewById(reviewId, memberId);
            return ResponseEntity.ok().body(reviewResponseDto);
        }

        reviewService.createReviewDislike(reviewId, memberId);
        ReviewResponseDto reviewResponseDto =  reviewService.findReviewById(reviewId, memberId);
        return ResponseEntity.ok().body(reviewResponseDto);
    }

}
