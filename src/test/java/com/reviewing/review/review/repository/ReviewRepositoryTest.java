package com.reviewing.review.review.repository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    void findReviewById() {

        Assertions.assertThat(reviewRepository.findReviewById(1L).getId()).isEqualTo(1L);

    }
}