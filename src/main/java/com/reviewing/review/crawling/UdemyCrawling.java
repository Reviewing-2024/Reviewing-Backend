package com.reviewing.review.crawling;

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
import org.openqa.selenium.JavascriptExecutor;
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
public class UdemyCrawling {

    private final PlatformRepository platformRepository;
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    @GetMapping("/udemy/platform")
    public void createPlatform() {
        Platform platform = new Platform("유데미");
        platformRepository.save(platform);
        Platform findPlatform = platformRepository.findByName("유데미");
        System.out.println(findPlatform.getName());
    }

    @GetMapping("/udemy/category")
    public void createCategory() {
        Platform findPlatform = platformRepository.findByName("유데미");

        Map<String, String> map = new HashMap<>();

        map.put("web-development", "웹 개발");
        map.put("data-science", "데이터 과학");
        map.put("mobile-apps", "모바일 개발");
        map.put("programming-languages", "프로그래밍 언어");
        map.put("game-development", "게임 개발");
        map.put("databases", "데이터 베이스 설계 및 개발");
        map.put("software-testing", "소프트웨어 테스팅");
        map.put("software-engineering", "소프트웨어 엔지니어링");
        map.put("development-tools", "소프트웨어 개발 도구");
        map.put("no-code-development", "노코드 개발");

        for (String slug : map.keySet()) {
            Category category = new Category(map.get(slug), slug, findPlatform);
            categoryRepository.save(category);
        }

        List<Category> categories = categoryRepository.findByPlatform(findPlatform);

        for (Category category : categories) {
            System.out.println(category.getName());
        }

    }

    @GetMapping("/udemy/crawling")
    public void crawling() {

        Platform findPlatform = platformRepository.findByName("유데미");

        ChromeOptions options = new ChromeOptions();
        // User-Agent 설정
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        // 헤드리스 모드
//        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);

        try {
            List<Category> categories = categoryRepository.findByPlatform(findPlatform);

            for (Category slugCategory : categories) {
                String categorySlug = slugCategory.getSlug();

                for (int page = 1; page <= 2; page++) {
//                    Thread.sleep(10000);
                    String url =
                            "https://www.udemy.com/courses/development/" + categorySlug + "/?p=" + page
                                    + "&sort=popularity";
                    driver.get(url);
                    Thread.sleep(10000);

                    String currentUrl = driver.getCurrentUrl();
                    if (currentUrl.contains("captcha")) {
                        log.error("ReCaptcha 페이지에 걸림: " + currentUrl);
                        return;
                    }

                    if (driver.getPageSource().trim().isEmpty()) {
                        log.error("2페이지 로드 실패: 빈 페이지 반환");
                        return;
                    }


                    try {
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

//                        Thread.sleep(10000);

                        // 전체 페이지 로드 완료 대기
//                        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
//                                .executeScript("return document.readyState").equals("complete"));

//                        Thread.sleep(10000);

                        // 특정 요소가 표시될 때까지 대기
                        wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("div.course-list_card__jWLES")
                        ));
//                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//                                By.cssSelector("div.course-card_price-text-container__Aw5Uy")
//                        ));
                        Thread.sleep(5000);
                        log.info("성공:"+ url);
                    } catch (TimeoutException e) {
                        log.info("에러: "+url);
                        return;
                    }

                    List<WebElement> courses = driver.findElements(
                            By.cssSelector("div.course-list_card__jWLES"));

                    int count = 0;
                    for (WebElement course : courses) {
                        count++;
                        String thumbnail = course.findElement(By.cssSelector(
                                        "img.course-card-image_image__iJLJg.course-card_course-image__iJtSQ.browse-course-card_image__XibHI"))
                                .getAttribute("src");

                        String title = course.findElement(By.cssSelector("h3.ud-heading-md.course-card-title_course-title___sH9w")).getText().split("\n")[0];
                        String courseUrl = course.findElement(By.cssSelector("h3.ud-heading-md.course-card-title_course-title___sH9w")).findElement(By.cssSelector("a"))
                                .getAttribute("href");
                        String[] parts = courseUrl.split("/");
                        String courseSlug = parts[parts.length - 1];
                        String teacher = course.findElement(
                                        By.cssSelector("div.course-card-instructors_instructor-list__helor"))
                                .getText();

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
                                    .thumbnailImage(thumbnail)
                                    .thumbnailVideo(null)
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
                        System.out.println(thumbnail);
                        System.out.println(teacher);
                        System.out.println("----------------------");
                    }
                    System.out.println(count);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

}
