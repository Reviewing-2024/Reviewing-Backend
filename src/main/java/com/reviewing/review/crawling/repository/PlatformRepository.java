package com.reviewing.review.crawling.repository;

import com.reviewing.review.course.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform,Long> {

    Platform findByName(String platform);

}
