package com.reviewing.review.review.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReviewRequestDto {

    private float rating;
    private String contents;

}
