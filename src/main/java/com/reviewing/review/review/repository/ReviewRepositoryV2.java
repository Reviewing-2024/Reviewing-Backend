package com.reviewing.review.review.repository;

import com.reviewing.review.review.entity.Review;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepositoryV2 extends JpaRepository<Review, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Review r WHERE r.id = :reviewId")
    Review findReviewByIdWithPessimisticLock(Long reviewId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT r FROM Review r WHERE r.id = :reviewId")
    Review findReviewByIdWithOptimisticLock(Long reviewId);

}
