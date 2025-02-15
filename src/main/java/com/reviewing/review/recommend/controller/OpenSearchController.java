package com.reviewing.review.recommend.controller;

import com.reviewing.review.recommend.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenSearchController {

    private final OpenSearchService openSearchService;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @PostMapping("/opensearch/index")
    public String createOpenSearchIndex(@RequestParam("indexName") String indexName) {
        openSearchService.deleteIndex(indexName);
        openSearchService.createIndex(indexName);
        openSearchService.updateFieldLimit(indexName);
        return "인덱스 생성 성공";
    }

    @PostMapping("/opensearch/courses")
    public String createEmbeddingsAndSaveOpenSearch(@RequestParam("indexName") String indexName,
            @RequestParam("value") String value) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("indexName", indexName)
                .addString("date", value)
                .toJobParameters();
        try {
            jobLauncher.run(jobRegistry.getJob("CourseSaveToOpenSearchJob"), jobParameters);
            return "성공";
        } catch (Exception e) {
            log.error("실패: {}", e.getMessage());
            return "실패";
        }
    }

    @PutMapping("/opensearch/courses")
    public String updateCourseEmbeddingsToOpenSearch(@RequestParam("indexName") String indexName,
            @RequestParam("value") String value) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("indexName", indexName)
                .addString("date",value)
                .toJobParameters();
        try {
            jobLauncher.run(jobRegistry.getJob("UpdateCourseToOpenSearchJob"), jobParameters);
            return "성공";
        } catch (Exception e) {
            return "실패";
        }
    }

}
