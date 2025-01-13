package com.reviewing.review.recommend.batch;

import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.recommend.domain.CourseOpenSearchRequestDto;
import com.reviewing.review.recommend.service.EmbeddingService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CourseSaveToOpenSearchBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryCourseRepository categoryCourseRepository;
    private final EmbeddingService embeddingService;
    private final OpenSearchClient openSearchClient;

    @Bean
    public Job CourseSaveToOpenSearchJob() {
        return new JobBuilder("CourseSaveToOpenSearchJob",jobRepository)
                .start(CourseSaveToOpenSearchStep())
                .build();
    }

    @Bean
    public Step CourseSaveToOpenSearchStep() {
        return new StepBuilder("CourseSaveToOpenSearchStep", jobRepository)
                .<Course, CourseOpenSearchRequestDto> chunk(10,platformTransactionManager)
                .reader(CourseSaveToOpenSearchReader())
                .processor(CourseSaveToOpenSearchProcessor())
                .writer(CourseSaveToOpenSearchwriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Course> CourseSaveToOpenSearchReader() {
        return new RepositoryItemReaderBuilder<Course>()
                .name("CourseSaveToOpenSearchReader")
                .pageSize(20)
                .methodName("findAll")
                .repository(courseCrawlingRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Course, CourseOpenSearchRequestDto> CourseSaveToOpenSearchProcessor() {
        return course -> {
            String title = course.getTitle();
            String platform = course.getPlatform().getName();
            BigDecimal rating = course.getRating();
            int wishes = course.getWishes();
            List<CategoryCourse> categories = categoryCourseRepository.findByCourse(course);
            String categoriesText = categories.stream()
                    .map(category -> category.getCategory().getName())
                    .collect(Collectors.joining(", "));
            log.info("카테고리: {}", categories);

            String inputCourseText = String.format(
                    "Title: %s, Platform: %s, Category: %s, Rating: %.1f, Wishes: %d",
                    title,
                    platform,
                    categoriesText,
                    rating,
                    wishes
            );
            log.info(inputCourseText);
            List<Double> embedding = embeddingService.generateEmbedding(inputCourseText);

            Map<String, Object> embeddingMap = new HashMap<>();
            for (int i = 0; i < embedding.size(); i++) {
                embeddingMap.put("dim_" + i, embedding.get(i)); // dim_0, dim_1, ...
            }

            // 문서 데이터 생성
            Map<String, Object> document = new HashMap<>();
            document.put("id", course.getId());
            document.put("embedding", embeddingMap);

            return new CourseOpenSearchRequestDto(
                    String.valueOf(course.getId()), document);
        };
    }

    @Bean
    public ItemWriter<CourseOpenSearchRequestDto> CourseSaveToOpenSearchwriter() {
        return courseOpenSearchRequestDtos -> {
            for (CourseOpenSearchRequestDto courseOpenSearchRequestDto : courseOpenSearchRequestDtos) {
                IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(builder -> builder
                        .index("course")   // 인덱스 이름
                        .id(String.valueOf(courseOpenSearchRequestDto.getCourseId())) // 문서 ID
                        .document(courseOpenSearchRequestDto.getDocument()) // 변환된 문서 데이터
                );
                openSearchClient.index(indexRequest);
            }
        };
    }

}
