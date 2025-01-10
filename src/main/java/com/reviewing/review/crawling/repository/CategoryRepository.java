package com.reviewing.review.crawling.repository;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.Platform;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByPlatform(Platform platform);

    Optional<Category> findBySlug(String slug);

}
