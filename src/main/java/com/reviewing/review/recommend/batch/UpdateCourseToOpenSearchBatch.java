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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UpdateCourseToOpenSearchBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CourseCrawlingRepository courseCrawlingRepository; //
    private final CategoryCourseRepository categoryCourseRepository;
    private final EmbeddingService embeddingService;
    private final OpenSearchClient openSearchClient;

    private int totalCnt = 0;

    @Bean
    public Job UpdateCourseToOpenSearchJob() {
        return new JobBuilder("UpdateCourseToOpenSearchJob",jobRepository)
                .start(UpdateCourseToOpenSearchStep())
                .build();
    }

    @Bean
    public Step UpdateCourseToOpenSearchStep() {
        return new StepBuilder("UpdateCourseToOpenSearchStep", jobRepository)
                .<Course, CourseOpenSearchRequestDto> chunk(5,platformTransactionManager)
                .reader(UpdateCourseToOpenSearchReader())
                .processor(UpdateCourseToOpenSearchProcessor(null))
                .writer(UpdateCourseToOpenSearchWriter(null))
                .build();
    }

    @Bean
    public RepositoryItemReader<Course> UpdateCourseToOpenSearchReader() {
        return new RepositoryItemReaderBuilder<Course>()
                .name("CourseSaveToOpenSearchReader")
                .pageSize(5)
                .arguments(List.of())
                .methodName("findByUpdatedTrue")
                .repository(courseCrawlingRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Course, CourseOpenSearchRequestDto> UpdateCourseToOpenSearchProcessor(@Value("#{jobParameters['indexName']}") String indexName) {
        return course -> {
            course.setUpdated(false);

            String title = course.getTitle();
            String platform = course.getPlatform().getName();
            String teacher = course.getTeacher();
            BigDecimal rating = course.getRating();
            int wishes = course.getWishes();
            List<CategoryCourse> categories = categoryCourseRepository.findByCourse(course);
            String categoriesText = categories.stream()
                    .map(category -> category.getCategory().getName())
                    .collect(Collectors.joining(", "));

            String inputCourseText = String.format(
                    "Title: %s, Platform: %s,Teacher: %s, Category: %s, Rating: %.1f, Wishes: %d",
                    title,
                    platform,
                    teacher,
                    categoriesText,
                    rating,
                    wishes
            );
            List<Double> embedding = embeddingService.generateEmbeddingV2(inputCourseText);

            // 문서 데이터 생성
            Map<String, Object> document = new HashMap<>();
            document.put("id", course.getId());
            document.put("embedding", embedding);
            document.put("title", title);
            document.put("teacher", teacher);

            return new CourseOpenSearchRequestDto(
                    String.valueOf(course.getId()), document);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<CourseOpenSearchRequestDto> UpdateCourseToOpenSearchWriter(@Value("#{jobParameters['indexName']}") String indexName) {
        return courseOpenSearchRequestDtos -> {
            for (CourseOpenSearchRequestDto courseOpenSearchRequestDto : courseOpenSearchRequestDtos) {
                IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(builder -> builder
                        .index(indexName)   // 인덱스 이름
                        .id(String.valueOf(courseOpenSearchRequestDto.getCourseId())) // 문서 ID
                        .document(courseOpenSearchRequestDto.getDocument()) // 변환된 문서 데이터
                );
                openSearchClient.index(indexRequest);
                totalCnt++;
            }
            log.info("{}개 저장중", totalCnt);
        };
    }

}
