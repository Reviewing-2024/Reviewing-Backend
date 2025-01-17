package com.reviewing.review.crawling.batch.codeit;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CodeitCrawling {

    private final PlatformRepository platformRepository;
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    @GetMapping("/codeit/platform")
    public void createPlatform() {
        Platform platform = new Platform("코드잇");
        platformRepository.save(platform);
        Platform findPlatform = platformRepository.findByName("코드잇");
        System.out.println(findPlatform.getName());
    }

    @GetMapping("/codeit/category")
    public void createCategory() {
        Platform findPlatform = platformRepository.findByName("코드잇");

        Map<String, String> map = new HashMap<>();

        map.put("FRONTEND", "프론트엔드");
//        map.put("BACKEND", "백엔드");
//        map.put("FULLSTACK", "풀스택");
//        map.put("DATA_ANALYSIS", "데이터 분석");
//        map.put("AI", "인공지능");
        map.put("data-engineering", "데이터 엔지니어링");

        for (String slug : map.keySet()) {
            Category category = new Category(map.get(slug), slug, findPlatform);
            categoryRepository.save(category);
        }

        List<Category> categories = categoryRepository.findByPlatform(findPlatform);

        for (Category category : categories) {
            System.out.println(category.getName());
        }

    }

    @GetMapping("/crawling/codeit")
    public void crawling() {

        Platform findPlatform = platformRepository.findByName("코드잇");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {
            List<Category> categories = categoryRepository.findByPlatform(findPlatform);

            for (Category slugCategory : categories) {
                System.out.println(slugCategory.getName());
                String categorySlug = slugCategory.getSlug();

                int lastPage = findLastPage(driver, categorySlug);
                if (lastPage == 0) {
                    continue;
                }
                log.info("마지막 페이지: {}", lastPage);
                int count = 0;
                for (int page = 1; page <= lastPage; page++) {

                    String url =
                            "https://www.codeit.kr/explore?page=" + page + "&category=" + categorySlug
                                    + "&difficulty=&types=";

                    driver.get(url);

                    new WebDriverWait(driver, Duration.ofSeconds(30)).
                            until(ExpectedConditions.visibilityOfElementLocated(
                                    By.cssSelector("div.TopicList_grid__7bZ8U")
                            ));

                    WebElement topic = driver.findElement(By.cssSelector("div.TopicList_grid__7bZ8U"));

                    List<WebElement> courses = topic.findElements(
                            By.cssSelector("div.TopicCommonCard_container__w89Rp"));

                    for (WebElement course : courses) {
                        count++;
                        String title = course.findElement(
                                By.cssSelector("p.TopicCommonCard_title__0KrCI")).getText();
                        String courseUrl = course.findElement(
                                By.cssSelector("a.TopicCommonCard_body__3_gHR")).getAttribute("href");
                        String[] parts = courseUrl.split("/");
                        String courseSlug = parts[parts.length - 1];

                        Optional<Course> findCourse = courseCrawlingRepository.findBySlug(courseSlug);
                        Optional<Category> findCategory = categoryRepository.findBySlug(categorySlug);

                        if (findCategory.isEmpty()) {
                            return;
                        }
                        Category category = findCategory.get();

                        if (findCourse.isPresent()) { // 강의가 있을 때

                            Optional<CategoryCourse> findCategoryCourse = categoryCourseRepository.findByCourseAndCategory(
                                    findCourse.get(), category);

                            if (findCategoryCourse.isEmpty()) { // 강의는 있는데 해당 카테고리에 없을 때
                                log.info("카테고리 추가");
                                CategoryCourse newCategoryCourse = CategoryCourse.builder()
                                        .category(category)
                                        .course(findCourse.get())
                                        .build();

                                categoryCourseRepository.save(newCategoryCourse);
                            } else {
                                log.info("이미 강의 존재");
                            }

                        } else {
                            log.info("강의 생성");
                            Course courseDto = Course.builder()
                                    .platform(findPlatform)
                                    .title(title)
                                    .url(courseUrl)
                                    .thumbnailImage("https://uni-reviewing.s3.ap-northeast-2.amazonaws.com/codeit.png")
                                    .thumbnailVideo(null)
                                    .teacher(null)
                                    .slug(courseSlug)
                                    .build();

                            Course savedCourse = courseCrawlingRepository.save(courseDto);

                            CategoryCourse categoryCourse = CategoryCourse.builder()
                                    .category(category)
                                    .course(savedCourse)
                                    .build();

                            categoryCourseRepository.save(categoryCourse);
                        }

                        System.out.println(title);
                        System.out.println(courseUrl);
                        System.out.println(courseSlug);
                        System.out.println("----------------------");

                    }
                }
                System.out.println(count);

            }

        } catch (Exception e) {
            log.error("크롤링 에러: {}", e.getMessage());
        } finally {
            driver.quit();
        }

    }

    private int findLastPage(WebDriver driver, String categorySlug) {

        String url = "https://www.codeit.kr/explore?page=1&category=" + categorySlug
                + "&difficulty=&types=";

        driver.get(url);

        new WebDriverWait(driver, Duration.ofSeconds(30)).
                until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.TopicList_grid__7bZ8U")
                ));

        WebElement paginationDiv = driver.findElement(By.cssSelector("div.Pagination_container__jFl63"));
        List<WebElement> pages = paginationDiv.findElements(
                By.cssSelector("button.Pagination_page__nB9XK"));

        if (!pages.isEmpty()) {
            return Integer.parseInt(pages.getLast().getText());
        }
        return 0;
    }

}

