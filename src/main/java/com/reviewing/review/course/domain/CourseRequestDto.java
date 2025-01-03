package com.reviewing.review.course.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseRequestDto {

    private String sort;
    private Long lastCourseId;
    private Float lastRating;
    private Integer lastComments;

}
