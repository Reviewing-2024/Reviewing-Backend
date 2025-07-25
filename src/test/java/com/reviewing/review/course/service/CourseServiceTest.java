package com.reviewing.review.course.service;

import static org.junit.jupiter.api.Assertions.*;

import com.reviewing.review.course.repository.CourseRepository;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseServiceTest {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseRepository courseRepository;

    @Test
    @DisplayName("강의 찜 생성 동시성 테스트")
    void createCourseWish() throws InterruptedException{
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        UUID courseId = UUID.fromString("15da9b51-7242-40e5-b517-73cb764155c9");;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    courseService.createCourseWish(courseId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(courseRepository.findCourseById
                (courseId).getWishes()).isEqualTo(100);
    }

    @Test
    @DisplayName("강의 찜 취소 동시성 테스트")
    void removeCourseWish() throws InterruptedException{
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        UUID courseId = UUID.fromString("15da9b51-7242-40e5-b517-73cb764155c9");;

        // when
        for (int i = 5; i <= 104; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    courseService.removeCourseWish(courseId, (long) finalI);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Assertions.assertThat(courseRepository.findCourseById
                (courseId).getWishes()).isEqualTo(0);
    }
}