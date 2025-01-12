package com.reviewing.review.crawling.batch.codeit;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

@Slf4j
public class CodeitReader implements ItemStreamReader<CrawlingCourseDto> {

    private WebDriver driver;
    private Iterator<CrawlingCourseDto> courseIterator;
    private Iterator<Category> categories;

    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;

    public CodeitReader(PlatformRepository platformRepository,
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

        Platform findPlatform = platformRepository.findByName("코드잇");
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
            int lastPage = findLastPage(driver, categorySlug);

            Platform findPlatform = platformRepository.findByName("코드잇");
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

                    CrawlingCourseDto courseDto = CrawlingCourseDto.builder()
                            .categorySlug(categorySlug)
                            .platform(findPlatform)
                            .title(title)
                            .courseUrl(courseUrl)
                            .thumbnailImage("https://uni-reviewing.s3.ap-northeast-2.amazonaws.com/codeit.png")
                            .thumbnailVideo(null)
                            .teacher(null)
                            .courseSlug(courseSlug)
                            .build();

                    courseDtoList.add(courseDto);
                }
            }
            log.info("카테고리: {}",categorySlug);
            log.info("마지막 페이지: {}",lastPage);
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
        return Integer.parseInt(pages.getLast().getText());
    }

}
