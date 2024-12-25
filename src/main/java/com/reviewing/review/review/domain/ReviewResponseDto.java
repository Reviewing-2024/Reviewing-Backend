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
    private long likes;
    private long dislikes;
    private boolean liked = false;
    private boolean disliked = false;
    private String createdAt;

    public ReviewResponseDto(Long id, String nickname, String contents, float rating,
            long likes, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.contents = contents;
        this.rating = rating;
        this.likes = likes;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

}
