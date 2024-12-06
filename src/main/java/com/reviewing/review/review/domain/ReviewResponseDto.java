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
    private boolean liked;
    private boolean disliked;
    private String createdAt;

    public ReviewResponseDto(Long id, String nickname, String contents, float rating,
            long likes, long dislikes, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.contents = contents;
        this.rating = rating;
        this.likes = (int) likes;
        this.dislikes = (int) dislikes;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

    public ReviewResponseDto(Long id, String nickname, String contents, float rating,
            long likes, long dislikes, boolean liked, boolean disliked, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.contents = contents;
        this.rating = rating;
        this.likes = (int) likes;
        this.dislikes = (int) dislikes;
        this.liked = liked;
        this.disliked = disliked;
        this.createdAt = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy.MM.dd hh.mm a", Locale.US));
    }

}
