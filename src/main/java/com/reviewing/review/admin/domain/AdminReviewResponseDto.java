package com.reviewing.review.admin.domain;

import com.reviewing.review.review.domain.ReviewStateType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminReviewResponseDto {

    private UUID courseId;
    private String courseTitle;
    private String courseTeacher;
    private String courseThumbnailImage;
    private String courseThumbnailVideo;
    private String courseUrl;

    private Long reviewId;
    private String reviewContents;
    private ReviewStateType reviewStatus;
    private String reviewCertification;
    private String updatedAt;

    public AdminReviewResponseDto(UUID courseId, String courseTitle, String courseTeacher,
            String courseThumbnailImage, String courseThumbnailVideo, String courseUrl,
            Long reviewId,
            String reviewContents, ReviewStateType reviewStatus, String reviewCertification,
            LocalDateTime updatedAt) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseTeacher = courseTeacher;
        this.courseThumbnailImage = courseThumbnailImage;
        this.courseThumbnailVideo = courseThumbnailVideo;
        this.courseUrl = courseUrl;
        this.reviewId = reviewId;
        this.reviewContents = reviewContents;
        this.reviewStatus = reviewStatus;
        this.reviewCertification = reviewCertification;
        if (updatedAt != null) {
            this.updatedAt = updatedAt.format(
                    DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
        } else {
            this.updatedAt = null; // 기본값
        }
    }
}