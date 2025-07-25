package com.reviewing.review.review.service;

import com.reviewing.review.review.repository.ReviewRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 좋아요 생성 동시성 테스트")
    void createReviewLike() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        Long reviewId = 1L;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    reviewService.createReviewLike(reviewId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(reviewRepository.findReviewById
                (reviewId).getLikes()).isEqualTo(100);

    }

    @Test
    @DisplayName("리뷰 좋아요 취소 동시성 테스트")
    void removeReviewLike() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        Long reviewId = 1L;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    reviewService.removeReviewLike(reviewId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(reviewRepository.findReviewById
                (reviewId).getLikes()).isEqualTo(0);

    }

    @Test
    @DisplayName("리뷰 싫어요 생성 동시성 테스트")
    void createReviewDislike() throws InterruptedException{
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        Long reviewId = 1L;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    reviewService.createReviewDislike(reviewId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(reviewRepository.findReviewById
                (reviewId).getDislikes()).isEqualTo(100);
    }

    @Test
    @DisplayName("리뷰 싫어요 취소 동시성 테스트")
    void removeReviewDislike() throws InterruptedException{
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        Long reviewId = 1L;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    reviewService.removeReviewDislike(reviewId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(reviewRepository.findReviewById
                (reviewId).getDislikes()).isEqualTo(0);
    }
}