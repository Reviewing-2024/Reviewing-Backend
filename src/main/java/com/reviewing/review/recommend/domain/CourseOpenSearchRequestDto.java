package com.reviewing.review.recommend.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseOpenSearchRequestDto {

    private String courseId;
    private Map<String, Object> document;

}
