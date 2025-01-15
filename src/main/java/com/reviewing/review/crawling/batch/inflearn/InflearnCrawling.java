package com.reviewing.review.crawling.batch.inflearn;

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
import org.openqa.selenium.TimeoutException;
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

//        map.put("web-dev", "웹 개발");
//        map.put("front-end", "프론트엔드");
//        map.put("back-end", "백엔드");
//        map.put("full-stack", "풀스택");
//        map.put("mobile-app", "모바일 앱 개발");
//        map.put("programming-lang", "프로그래밍 언어");
//        map.put("algorithm", "알고리즘/자료구조");
//        map.put("database-dev", "데이터베이스");
//        map.put("devops-infra", "데브옵스/인프라");
        map.put("sw-test", "소프트웨어 테스트");
//        map.put("programming-tool", "개발 도구");
//        map.put("web-publishing", "웹 퍼블리싱");
//        map.put("desktop-application", "데스크톱 앱 개발");
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

    @GetMapping("/crawling/inflearn")
    public void crawling() {

        Platform findPlatform = platformRepository.findByName("인프런");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--auto-open-devtools-for-tabs");

        WebDriver driver = new ChromeDriver(options);

        try {
            List<Category> categories = categoryRepository.findByPlatform(findPlatform);

            for (Category slugCategory : categories) {
                String categorySlug = slugCategory.getSlug();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                int lastPage = findLastPage(driver, categorySlug, wait);
                if (lastPage == 0) {
                    continue;
                }
                log.info("마지막 페이지: {}", lastPage);
                for (int page = 1; page <= lastPage; page++) {
                    String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                            + "?types=ONLINE&page_number=" + page;

                    driver.get(url);

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
            log.error("크롤링 에러: {}", e.getMessage());
        } finally {
            driver.quit();
        }

    }

    private int findLastPage(WebDriver driver, String categorySlug, WebDriverWait wait) {
        String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                + "?types=ONLINE&page_number=1";

        driver.get(url);

        scrollToElement(driver,wait);

        WebElement paginationDiv = driver.findElement(
                By.cssSelector("div.mantine-Group-root.mantine-13v5ff3"));
        List<WebElement> pages = paginationDiv.findElements(
                By.cssSelector("button.mantine-UnstyledButton-root.mantine-Pagination-control.mantine-1gh1g76"));

        if (!pages.isEmpty()) {
            return Integer.parseInt(pages.getLast().getText());
        }
        return 0;
    }

    private void scrollToElement(WebDriver driver, WebDriverWait wait) {
        boolean isElementVisible = false;

        while (!isElementVisible) {
            try {
                wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
                wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.querySelector('div.mantine-Group-root.mantine-13v5ff3') !== null"));

                // 요소가 화면에 보이는지 확인
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.mantine-Group-root.mantine-13v5ff3")));

                // 요소가 보인다면 스크롤 완료
                isElementVisible = true;

                // 요소가 보이도록 스크롤
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                System.out.println("Element is visible: " + "div.mantine-Group-root.mantine-13v5ff3");

            } catch (TimeoutException e) {
                // 현재 화면에서 요소가 보이지 않는 경우 스크롤
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 10000);");
                System.out.println("Scrolling down...");
            }
        }
    }


}
