package com.reviewing.review.recommend.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendResponseDto {

    private UUID courseId;
    private String courseTitle;
    private String courseTeacher;
    private String courseUrl;
    private String courseSlug;

}
