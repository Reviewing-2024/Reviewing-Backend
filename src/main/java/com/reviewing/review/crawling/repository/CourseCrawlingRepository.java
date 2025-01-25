package com.reviewing.review.crawling.repository;

import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.entity.Platform;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCrawlingRepository extends JpaRepository<Course, UUID> {

    Optional<Course> findBySlug(String slug);

    List<Course> findByPlatform(Platform platform);

    Page<Course> findByUpdatedTrue(Pageable pageable);
}
