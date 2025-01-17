package com.reviewing.review.crawling.batch.fastcampus;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.CategoryCourse;
import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.repository.CategoryCourseRepository;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.CourseCrawlingRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
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
import org.openqa.selenium.interactions.Actions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FastcampusCrawling {

    private final PlatformRepository platformRepository;
    private final CourseCrawlingRepository courseCrawlingRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryCourseRepository categoryCourseRepository;

    @GetMapping("/fastcampus/platform")
    public void createPlatform() {
        Platform platform = new Platform("패스트캠퍼스");
        platformRepository.save(platform);
        Platform findPlatform = platformRepository.findByName("패스트캠퍼스");
        System.out.println(findPlatform.getName());
    }

    @GetMapping("/fastcampus/category")
    public void createCategory() {
        Platform findPlatform = platformRepository.findByName("패스트캠퍼스");

        Map<String, String> map = new HashMap<>();

        map.put("category_online_programmingfront", "프론트엔드");
//        map.put("category_online_programmingback", "백엔드");
//        map.put("category_online_programmingapp", "모바일 앱 개발");
//        map.put("category_online_programmingdevops", "DevOps/Infra");
//        map.put("category_online_programmingblockchain", "블록체인 개발");
        map.put("category_online_programminggame", "게임 개발");
//        map.put("category_online_programmingcareer", "컴퓨터 공학/SW 엔지니어링");
//        map.put("category_online_datasciencedeep", "딥러닝");
//        map.put("category_online_datasciencecv", "컴퓨터비전");
//        map.put("category_online_datasciencenlp", "자연어처리");
//        map.put("category_online_datascienceanal", "데이터분석");
//        map.put("category_online_datasciencemr", "머신러닝");
//        map.put("category_online_datascienceeng", "데이터엔지니어링");

        for (String slug : map.keySet()) {
            Category category = new Category(map.get(slug), slug, findPlatform);
            categoryRepository.save(category);
        }

        List<Category> categories = categoryRepository.findByPlatform(findPlatform);

        for (Category category : categories) {
            System.out.println(category.getName());
        }

    }

    @GetMapping("/fastcampus/crawling")
    public void crawling() {

        Platform findPlatform = platformRepository.findByName("패스트캠퍼스");

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
                String categorySlug = slugCategory.getSlug();

                String url = "https://fastcampus.co.kr/" + categorySlug;

                driver.get(url);

                Actions actions = new Actions(driver);

                int oldCount = 0;
                int scrollCount = 0;
                while (true) {
                    // 현재 아이템 수 확인
                    List<WebElement> courses = driver.findElements(By.cssSelector(
                            "div.CourseCard_basicType__XWiBm.CourseCard_courseCardContainer__sx20K.InfinityCourse_infinityCourseCard__YxLps"));

                    int newCount = courses.size();

                    System.out.println("newCount: "+newCount);

                    // 새 아이템이 안 늘어나면 종료
                    if (newCount == oldCount) {
                        break;
                    }
                    oldCount = newCount;

                    // 스크롤 1000px씩
                    actions.scrollByAmount(0, 5000).perform();

                    // 로딩 대기
                    Thread.sleep(6000);

                    scrollCount++;
                    if (scrollCount > 50) { // 최대 20번 스크롤
                        break;
                    }
                }

                List<WebElement> courses = driver.findElements(By.cssSelector(
                        "div.CourseCard_basicType__XWiBm.CourseCard_courseCardContainer__sx20K.InfinityCourse_infinityCourseCard__YxLps"));

                int count = 0;
                for (WebElement course : courses) {
                    count++;

                    String title = course.findElement(By.cssSelector("span.CourseCard_courseCardTitle__1HQgO")).getText();
                    String courseUrl = course.findElement(By.cssSelector("a.CourseCard_courseCardDetailContainer__PnVam")).getAttribute("href");
                    String[] parts = courseUrl.split("/");
                    String courseSlug = parts[parts.length - 1];
                    String thumbnail = course.findElement(By.cssSelector("img.CourseCard_courseCardImage__XcpZb")).getAttribute("src");

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
                    System.out.println(thumbnail);
                    System.out.println("----------------------");

                }
                System.out.println(count);

            }

        } catch (Exception e) {
            log.error("크롤링 에러: {}", e.getMessage());
        } finally {
            driver.quit();
        }
    }

}
