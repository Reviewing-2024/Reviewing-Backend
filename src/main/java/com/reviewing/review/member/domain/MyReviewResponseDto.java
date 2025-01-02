package com.reviewing.review.member.domain;

import com.reviewing.review.review.domain.ReviewStateType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyReviewResponseDto {

    private Long reviewId;
    private Long courseId;
    private String courseTitle;
    private String courseSlug;
    private String contents;
    private ReviewStateType status;
    private float rating;
    private long likes;
    private long dislikes;
    private String rejectionReason;
    private String createdAt;

    public MyReviewResponseDto(Long reviewId, Long courseId, String courseTitle, String courseSlug, String contents,
            ReviewStateType status,
            float rating,
            long likes, String rejectionReason, LocalDateTime createdAt) {

        this.reviewId = reviewId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseSlug = courseSlug;
        this.contents = contents;
        this.status = status;
        this.rating = rating;
        this.likes = likes;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

}
