package com.reviewing.review.review.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private String nickname;
    private String contents;
    private float rating;
    private int likes;
    private int dislikes;
    private boolean liked = false;
    private boolean disliked = false;
    private String createdAt;

    public ReviewResponseDto(Long id, String nickname, String contents, float rating,
            int likes, int dislikes, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.contents = contents;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
        this.createdAt = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

}
