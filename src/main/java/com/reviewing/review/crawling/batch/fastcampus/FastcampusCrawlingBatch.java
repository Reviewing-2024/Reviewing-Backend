package com.reviewing.review.crawling.batch.fastcampus;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.crawling.domain.CategoryCourseDto;
import com.reviewing.review.crawling.domain.CrawlingCourseDto;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FastcampusCrawlingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final CourseCrawlingRepository courseCrawlingRepository;
    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    @Bean
    public Job fastcampusJob() {
        return new JobBuilder("fastcampusCrawlingJob",jobRepository)
                .start(fastcampusStep())
                .build();
    }

    @Bean
    public Step fastcampusStep() {
        return new StepBuilder("fastcampusCrawlingStep", jobRepository)
                .<CrawlingCourseDto, CategoryCourseDto> chunk(10,platformTransactionManager)
                .reader(fastcampusReader())
                .processor(fastcampusProcessor())
                .writer(fastcampusWriter())
                .faultTolerant()
                .retryLimit(5)
                .retry(NoSuchElementException.class)
                .skip(NoSuchElementException.class)
                .skipLimit(5)
                .build();
    }

    @Bean
    public ItemStreamReader<CrawlingCourseDto> fastcampusReader() {
        return new FastcampusReader(platformRepository, categoryRepository);
    }

    @Bean
    public ItemProcessor<CrawlingCourseDto, CategoryCourseDto> fastcampusProcessor() {
        return crawlingCourseDto -> {
            Optional<Course> findCourse = courseCrawlingRepository.findBySlug(crawlingCourseDto.getCourseSlug());
            Optional<Category> findCategory = categoryRepository.findBySlug(crawlingCourseDto.getCategorySlug());

            if (findCategory.isEmpty()) {
                return  null;
            }
            Category category = findCategory.get();

            if (findCourse.isPresent()){ // 강의 존재
                Optional<CategoryCourse> findCategoryCourse = categoryCourseRepository.findByCourseAndCategory(
                        findCourse.get(), category);
                if (findCategoryCourse.isEmpty()) {
                    // 카테고리-강의 매칭 데이터 없음
                    CategoryCourse newCategoryCourse = CategoryCourse.builder()
                            .category(category)
                            .course(findCourse.get())
                            .build();
                    categoryCourseRepository.save(newCategoryCourse);
                    return null;
                } else {
                    // 이미 강의 존재 + 카테고리-강의 매칭 데이터 존재
                    return null;
                }
            } else {
                // 강의 데이터 없음
                Course courseDto = Course.builder()
                        .platform(crawlingCourseDto.getPlatform())
                        .title(crawlingCourseDto.getTitle())
                        .url(crawlingCourseDto.getCourseUrl())
                        .thumbnailImage(crawlingCourseDto.getThumbnailImage())
                        .thumbnailVideo(crawlingCourseDto.getThumbnailVideo())
                        .teacher(crawlingCourseDto.getTeacher())
                        .slug(crawlingCourseDto.getCourseSlug())
                        .build();

                return new CategoryCourseDto(category, courseDto);
            }
        };
    }

    @Bean
    public ItemWriter<CategoryCourseDto> fastcampusWriter() {
        return categoryCourseDtos -> {
            for (CategoryCourseDto categoryCourseDto : categoryCourseDtos) {
                Course newCourse = courseCrawlingRepository.save(categoryCourseDto.getCourse());
                CategoryCourse newCategoryCourse = CategoryCourse.builder()
                        .category(categoryCourseDto.getCategory())
                        .course(newCourse)
                        .build();
                categoryCourseRepository.save(newCategoryCourse);
            }
        };
    }

}
