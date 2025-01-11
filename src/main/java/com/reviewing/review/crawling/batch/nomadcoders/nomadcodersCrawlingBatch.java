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
    public Job job() {
        return new JobBuilder("nomadcodersCrawlingJob",jobRepository)
                .start(step())
                .build();
    }

    @Bean
    public Step step() {

        return new StepBuilder("nomadcodersCrawlingStep",jobRepository)
                .<Course,Course> chunk(5,platformTransactionManager)
                .reader(beforeReader())
                .processor(processor())
                .writer(afterWriter())
                .faultTolerant()
                .retryLimit(5)
                .retry(NoSuchElementException.class)
                .skip(NoSuchElementException.class)
                .skipLimit(5)
                .build();
    }

    @Bean
    public ItemStreamReader<Course> beforeReader() {
        return new NomadcodersReader(platformRepository);
    }

    @Bean
    public ItemProcessor<Course, Course> processor() {
        return course -> {
            Optional<Course> findCourse = courseCrawlingRepository.findBySlug(course.getSlug());
            if (findCourse.isPresent()) {
                return null;
            }
            return course;
        };
    }

    @Bean
    public ItemWriter<Course> afterWriter() {
        return courses -> {
            for (Course course : courses) {
                courseCrawlingRepository.save(course);
            }
        };
    }

}
