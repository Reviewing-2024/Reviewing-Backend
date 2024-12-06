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

    private Long id;
    private Long courseId;
    private String contents;
    private ReviewStateType status;
    private float rating;
    private int likes;
    private int dislikes;
    private String rejectionReason;
    private String createdAt;

    public MyReviewResponseDto(Long id, Long courseId, String contents, ReviewStateType status,
            float rating,
            long likes, long dislikes, String rejectionReason, LocalDateTime createdAt) {

        this.id = id;
        this.courseId = courseId;
        this.contents = contents;
        this.status = status;
        this.rating = rating;
        this.likes = (int) likes;
        this.dislikes = (int) dislikes;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

}
