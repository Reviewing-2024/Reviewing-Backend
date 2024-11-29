package com.reviewing.review.admin.controller;

import com.reviewing.review.admin.domain.AdminReviewResponseDto;
import com.reviewing.review.admin.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/reviews")
    public ResponseEntity<List<AdminReviewResponseDto>> findReviewByStatus(
            @RequestParam(value = "status", required = true) String status) {

        List<AdminReviewResponseDto> reviews = adminService.findReviewByStatus(status);

        return ResponseEntity.ok().body(reviews);
    }

    @PatchMapping("/reviews/{reviewId}/approve")
    public void changeReviewApprove(@PathVariable Long reviewId) {
        adminService.changeReviewApprove(reviewId);
    }

    @PatchMapping("/reviews/{reviewId}/reject")
    public void changeReviewReject(@PathVariable Long reviewId) {
        adminService.changeReviewReject(reviewId);
    }


}
