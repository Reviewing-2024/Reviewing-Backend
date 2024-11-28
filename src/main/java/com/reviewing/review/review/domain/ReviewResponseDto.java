package com.reviewing.review.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private String nickname;
    private String contents;
    private float rating;
//    private int likes;
//    private int dislikes;
//    private String createdAt;

}
