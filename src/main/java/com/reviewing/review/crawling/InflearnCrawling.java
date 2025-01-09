package com.reviewing.review.crawling;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
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
public class InflearnCrawling {

    private final PlatformRepository platformRepository;
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    @GetMapping("/inflearn/platform")
    public void createPlatform() {
        Platform platform = new Platform("인프런");
        platformRepository.save(platform);
        Platform findPlatform = platformRepository.findByName("인프런");
        System.out.println(findPlatform.getName());
    }

    @GetMapping("/inflearn/category")
    public void createCategory() {
        Platform findPlatform = platformRepository.findByName("인프런");

        Map<String, String> map = new HashMap<>();

        map.put("web-dev", "웹 개발");
        map.put("front-end", "프론트엔드");
        map.put("back-end", "백엔드");
        map.put("full-stack", "풀스택");
        map.put("mobile-app", "모바일 앱 개발");
        map.put("programming-lang", "프로그래밍 언어");
        map.put("algorithm", "알고리즘/자료구조");
        map.put("database-dev", "데이터베이스");
        map.put("devops-infra", "데브옵스/인프라");
        map.put("sw-test", "소프트웨어 테스트");
        map.put("programming-tool", "개발 도구");
        map.put("web-publishing", "웹 퍼블리싱");
        map.put("desktop-application", "데스크톱 앱 개발");
        map.put("vr-ar", "VR/AR");

        for (String slug : map.keySet()) {
            Category category = new Category(map.get(slug), slug, findPlatform);
            categoryRepository.save(category);
        }

        List<Category> categories = categoryRepository.findByPlatform(findPlatform);

        for (Category category : categories) {
            System.out.println(category.getName());
        }

    }

    @GetMapping("/inflearn/crawling")
    public void crawling() {

        Platform findPlatform = platformRepository.findByName("인프런");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {

            List<Category> categories = categoryRepository.findByPlatform(findPlatform);

            for (Category slugCategory : categories) {
                String categorySlug = slugCategory.getSlug();

                for (int page = 1; page <= 1; page++) {
                    String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                            + "?types=ONLINE&page_number=" + page;

                    driver.get(url);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                    // 전체 페이지 로드 완료 대기
                    wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));

                    // 특정 요소가 표시될 때까지 대기
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("ul.css-sdr7qd.mantine-1avyp1d")
                    ));

                    WebElement firstUl = driver.findElement(By.cssSelector("ul.css-sdr7qd.mantine-1avyp1d"));
                    List<WebElement> firstCourses = firstUl.findElements(By.cssSelector("li.css-8atqhb.mantine-1avyp1d"));
                    System.out.println("first count: " + firstCourses.size());

                    WebElement secondUrl = driver.findElement(By.cssSelector("ul.css-2ldd65.mantine-1avyp1d"));
                    List<WebElement> secondCourses = secondUrl.findElements(By.cssSelector("li.mantine-1avyp1d"));
                    System.out.println("second count: " + secondCourses.size());

                    for (WebElement course : firstCourses) {
                        String baseCourseUrl = course.findElement(By.tagName("a")).getAttribute("href");
                        int keywordIndex = baseCourseUrl.indexOf("attributionToken");
                        String encodedUrl = (keywordIndex != -1) ? baseCourseUrl.substring(0, baseCourseUrl.lastIndexOf("?", keywordIndex)) : baseCourseUrl;
                        String courseUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
                        String[] parts = courseUrl.split("/");
                        String courseSlug = parts[parts.length - 1];

                        String thumbnailImage = null;
                        String thumbnailVideo = null;
                        try {
                            thumbnailImage = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd")).findElement(By.tagName("img")).getAttribute("src");
                        } catch (NoSuchElementException e) {
                            log.info("비디오 썸네일");
                            thumbnailVideo = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd")).findElement(By.tagName("source")).getAttribute("src");
                        }

                        String title = course.findElement(By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22")).getText();

                        String teacher = course.findElement(By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth")).getText();

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
                                    .thumbnailImage(thumbnailImage)
                                    .thumbnailVideo(thumbnailVideo)
                                    .teacher(teacher)
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
                        System.out.println(thumbnailImage);
                        System.out.println(thumbnailVideo);
                        System.out.println(teacher);
                        System.out.println("----------------------");

                    }

                    for (WebElement course : secondCourses) {
                        String baseCourseUrl = course.findElement(By.tagName("a")).getAttribute("href");
                        int keywordIndex = baseCourseUrl.indexOf("attributionToken");
                        String encodedUrl = (keywordIndex != -1) ? baseCourseUrl.substring(0, baseCourseUrl.lastIndexOf("?", keywordIndex)) : baseCourseUrl;
                        String courseUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
                        String[] parts = courseUrl.split("/");
                        String courseSlug = parts[parts.length - 1];

                        String thumbnailImage = null;
                        String thumbnailVideo = null;
                        try {
                            thumbnailImage = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                                    .findElement(By.tagName("img")).getAttribute("src");
                        } catch (NoSuchElementException e) {
                            log.info("비디오 썸네일");
                            thumbnailVideo = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                                    .findElement(By.tagName("source")).getAttribute("src");
                        }

                        String title = course.findElement(By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22")).getText();

                        String teacher = course.findElement(By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth")).getText();

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
                                    .thumbnailImage(thumbnailImage)
                                    .thumbnailVideo(thumbnailVideo)
                                    .teacher(teacher)
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
                        System.out.println(thumbnailImage);
                        System.out.println(thumbnailVideo);
                        System.out.println(teacher);
                        System.out.println("----------------------");

                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

    }

}
