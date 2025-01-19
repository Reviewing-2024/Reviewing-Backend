package com.reviewing.review.crawling.batch.fastcampus;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.domain.CrawlingCourseDto;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

@Slf4j
public class FastcampusReader implements ItemStreamReader<CrawlingCourseDto> {

    private WebDriver driver;
    private Iterator<CrawlingCourseDto> courseIterator;
    private Iterator<Category> categories;

    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;

    public FastcampusReader(PlatformRepository platformRepository,
            CategoryRepository categoryRepository) {
        this.platformRepository = platformRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        this.driver = new ChromeDriver(options);

        Platform findPlatform = platformRepository.findByName("패스트캠퍼스");
        this.categories = categoryRepository.findByPlatform(findPlatform).iterator();
    }

    @Override
    public CrawlingCourseDto read() throws NoSuchElementException {
        if (courseIterator != null && courseIterator.hasNext()) {
            return courseIterator.next();
        }
        if (categories.hasNext()) {
            List<CrawlingCourseDto> courseDtoList = new ArrayList<>();
            String categorySlug = categories.next().getSlug();
            String url = "https://fastcampus.co.kr/" + categorySlug;
            driver.get(url);
            Actions actions = new Actions(driver);
            Platform findPlatform = platformRepository.findByName("패스트캠퍼스");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            int oldCount = 0;
            int scrollCount = 0;
            while (true) {
                // 현재 아이템 수 확인
                List<WebElement> courses = driver.findElements(By.cssSelector(
                        "div.CourseCard_basicType__XWiBm.CourseCard_courseCardContainer__sx20K.InfinityCourse_infinityCourseCard__YxLps"));

                int newCount = courses.size();
                // 새 아이템이 안 늘어나면 종료
                if (newCount == oldCount) {
                    break;
                }
                oldCount = newCount;

                // 스크롤 5000px씩
                actions.scrollByAmount(0, 5000).perform();

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    log.info("패스트캠퍼스 sleep 에러");
                    throw new RuntimeException(e);
                }

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("div.CourseCard_basicType__XWiBm.CourseCard_courseCardContainer__sx20K.InfinityCourse_infinityCourseCard__YxLps")
                ));

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

                String title = course.findElement(
                        By.cssSelector("span.CourseCard_courseCardTitle__1HQgO")).getText();
                String courseUrl = course.findElement(
                                By.cssSelector("a.CourseCard_courseCardDetailContainer__PnVam"))
                        .getAttribute("href");
                String[] parts = courseUrl.split("/");
                String courseSlug = parts[parts.length - 1];
                String thumbnail = course.findElement(
                                By.cssSelector("img.CourseCard_courseCardImage__XcpZb"))
                        .getAttribute("src");

                CrawlingCourseDto courseDto = CrawlingCourseDto.builder()
                        .categorySlug(categorySlug)
                        .platform(findPlatform)
                        .title(title)
                        .courseUrl(courseUrl)
                        .thumbnailImage(thumbnail)
                        .thumbnailVideo(null)
                        .teacher(null)
                        .courseSlug(courseSlug)
                        .build();

                courseDtoList.add(courseDto);
            }
            log.info("카테고리: {}",categorySlug);
            log.info("강의수: {}",count);
            courseIterator = courseDtoList.iterator();
        }
        if (courseIterator == null || !courseIterator.hasNext()) {
            return null;
        }
        return courseIterator.next();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.error("Step close 에러: {}", e.getMessage());
            }
        }
    }
}
