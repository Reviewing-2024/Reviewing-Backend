package com.reviewing.review.crawling.batch.nomadcoders;

import com.reviewing.review.course.entity.Course;
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
public class nomadcodersCrawlingBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final CourseCrawlingRepository courseCrawlingRepository;
    private final PlatformRepository platformRepository;

    @Bean
    public Job nomadcodsersJob() {
        return new JobBuilder("nomadcodersCrawlingJob",jobRepository)
                .start(nomadcodersStep())
                .build();
    }

    @Bean
    public Step nomadcodersStep() {

        return new StepBuilder("nomadcodersCrawlingStep",jobRepository)
                .<Course,Course> chunk(5,platformTransactionManager)
                .reader(nomadcodersReader())
                .processor(nomadcodersProcessor())
                .writer(nomadcodersWriter())
                .faultTolerant()
                .retryLimit(5)
                .retry(NoSuchElementException.class)
                .skip(NoSuchElementException.class)
                .skipLimit(5)
                .build();
    }

    @Bean
    public ItemStreamReader<Course> nomadcodersReader() {
        return new NomadcodersReader(platformRepository);
    }

    @Bean
    public ItemProcessor<Course, Course> nomadcodersProcessor() {
        return course -> {
            Optional<Course> findCourse = courseCrawlingRepository.findBySlug(course.getSlug());
            if (findCourse.isPresent()) {
                log.info("존재하는 강의");
                return null;
            }
            log.info("processor");
            return course;
        };
    }

    @Bean
    public ItemWriter<Course> nomadcodersWriter() {
        return courses -> {
            log.info("writer");
            for (Course course : courses) {
                log.info("강의 저장");
                courseCrawlingRepository.save(course);
            }
        };
    }

}
