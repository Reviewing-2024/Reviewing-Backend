package com.reviewing.review.review.domain;

public enum ReviewStateType {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String state;

    private ReviewStateType(String state) {
        this.state = state;
    }

    public String getReviewState() {
        return this.state;
    }

}
