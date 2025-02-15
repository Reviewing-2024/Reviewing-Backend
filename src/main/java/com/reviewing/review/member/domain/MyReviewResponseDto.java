package com.reviewing.review.member.domain;

import com.reviewing.review.review.domain.ReviewStateType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyReviewResponseDto {

    private Long reviewId;
    private UUID courseId;
    private String courseTitle;
    private String courseSlug;
    private String contents;
    private ReviewStateType status;
    private BigDecimal rating;
    private int likes;
    private int dislikes;
    private String rejectionReason;
    private String createdAt;

    public MyReviewResponseDto(Long reviewId, UUID courseId, String courseTitle, String courseSlug, String contents,
            ReviewStateType status,
            BigDecimal rating,
            int likes,int dislikes, String rejectionReason, LocalDateTime createdAt) {

        this.reviewId = reviewId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseSlug = courseSlug;
        this.contents = contents;
        this.status = status;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm a", Locale.US));
    }

}
