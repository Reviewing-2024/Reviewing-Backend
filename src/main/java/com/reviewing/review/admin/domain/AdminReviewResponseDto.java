package com.reviewing.review.admin.domain;

import com.reviewing.review.review.domain.ReviewStateType;
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

}