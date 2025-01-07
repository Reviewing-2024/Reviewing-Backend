package com.reviewing.review.review.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewStateByMemberDto {

    private UUID courseId;
    private Long reviewId;
    private Long memberId;
    private ReviewStateType reviewState;

}
