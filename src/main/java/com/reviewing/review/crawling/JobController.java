package com.reviewing.review.crawling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/allCrawling")
    public String runAllJobs(@RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        try {
            jobLauncher.run(jobRegistry.getJob("nomadcodersCrawlingJob"), jobParameters);
        } catch (Exception e) {
            log.error("Job 살패", e);
        }
        try {
            jobLauncher.run(jobRegistry.getJob("codeitCrawlingJob"), jobParameters);
        } catch (Exception e) {
            log.error("Job 살패", e);
        }
        try {
            jobLauncher.run(jobRegistry.getJob("fastcampusCrawlingJob"), jobParameters);
        } catch (Exception e) {
            log.error("Job 살패", e);
        }
        try {
            jobLauncher.run(jobRegistry.getJob("inflearnCrawlingJob"), jobParameters);
        } catch (Exception e) {
            log.error("Job 살패", e);
        }
        return "ok";
    }

    @GetMapping("/nomadcodersCrawling")
    public String nomadcodersCrawling(@RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("nomadcodersCrawlingJob"), jobParameters);
        return "ok";
    }

    @GetMapping("/codeitCrawling")
    public String codeitCrawling(@RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("codeitCrawlingJob"), jobParameters);
        return "ok";
    }

    @GetMapping("/fastcampusCrawling")
    public String fastcampusCrawling(@RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("fastcampusCrawlingJob"), jobParameters);
        return "ok";
    }

    @GetMapping("/inflearnCrawling")
    public String inflearnCrawling(@RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("inflearnCrawlingJob"), jobParameters);
        return "ok";
    }

    @GetMapping("/inflearnCrawling/category")
    public String inflearnCrawlingByCategory(@RequestParam("categorySlug") String categorySlug,
            @RequestParam("value") String value) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("categorySlug", categorySlug)
                .addString("date", value)
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("inflearnCrawlingByCategoryJob"), jobParameters);
        return "ok";
    }

}
