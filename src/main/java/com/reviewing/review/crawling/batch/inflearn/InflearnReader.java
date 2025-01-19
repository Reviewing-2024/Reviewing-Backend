package com.reviewing.review.crawling.batch.inflearn;

import com.reviewing.review.course.entity.Category;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.domain.CrawlingCourseDto;
import com.reviewing.review.crawling.repository.CategoryRepository;
import com.reviewing.review.crawling.repository.PlatformRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

@Slf4j
public class InflearnReader implements ItemStreamReader<CrawlingCourseDto> {

    private WebDriver driverForCourseData;
    private WebDriver driverForLastPage;
    private Iterator<CrawlingCourseDto> courseIterator;
    private Iterator<Category> categories;
    private Iterator<Integer> pageIterator;
    private String categorySlug;

    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;

    public InflearnReader(PlatformRepository platformRepository,
            CategoryRepository categoryRepository) {
        this.platformRepository = platformRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        ChromeOptions optionsForCourseData = new ChromeOptions();
        optionsForCourseData.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        optionsForCourseData.addArguments("--headless");
        optionsForCourseData.addArguments("--disable-popup-blocking");
        optionsForCourseData.addArguments("--disable-default-apps");
        optionsForCourseData.addArguments("--disable-notifications");
        optionsForCourseData.addArguments("--disable-blink-features=AutomationControlled");
        optionsForCourseData.addArguments("--no-sandbox");
        optionsForCourseData.addArguments("--disable-gpu");

        this.driverForCourseData = new ChromeDriver(optionsForCourseData);

        ChromeOptions optionsForLastPage = new ChromeOptions();
        optionsForLastPage.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        optionsForLastPage.addArguments("--headless");
        optionsForLastPage.addArguments("--disable-popup-blocking");
        optionsForLastPage.addArguments("--disable-default-apps");
        optionsForLastPage.addArguments("--disable-notifications");
        optionsForLastPage.addArguments("--disable-blink-features=AutomationControlled");
        optionsForLastPage.addArguments("--disable-gpu");
        optionsForLastPage.addArguments("--disable-extensions");
        optionsForLastPage.addArguments("--auto-open-devtools-for-tabs");
        optionsForLastPage.addArguments("--no-sandbox");

        this.driverForLastPage = new ChromeDriver(optionsForLastPage);

        Platform findPlatform = platformRepository.findByName("인프런");
        this.categories = categoryRepository.findByPlatform(findPlatform).iterator();
    }

    @Override
    public CrawlingCourseDto read() throws NoSuchElementException {
        if (courseIterator != null && courseIterator.hasNext()) {
            return courseIterator.next();
        }

        if (pageIterator != null && pageIterator.hasNext()) {
            WebDriverWait wait = new WebDriverWait(driverForCourseData, Duration.ofSeconds(30));
            Platform findPlatform = platformRepository.findByName("인프런");
            inflearnCrawlingByOnePage(pageIterator.next(), wait, findPlatform);
            return courseIterator.next();
        }
        if (categories.hasNext()) {
            categorySlug = categories.next().getSlug();
            log.info("카테고리: {}", categorySlug);
            WebDriverWait waitForCourseData = new WebDriverWait(driverForCourseData, Duration.ofSeconds(30));
            WebDriverWait waitForLastPage = new WebDriverWait(driverForLastPage, Duration.ofSeconds(30));
            findLastPage(driverForLastPage, categorySlug, waitForLastPage);
            Platform findPlatform = platformRepository.findByName("인프런");
            inflearnCrawlingByOnePage(pageIterator.next(), waitForCourseData, findPlatform);
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
        if (driverForCourseData != null) {
            try {
                driverForCourseData.quit();
            } catch (Exception e) {
                log.error("Step close 에러: {}", e.getMessage());
            }
        }
    }

    private void findLastPage(WebDriver driver, String categorySlug, WebDriverWait wait) {
        String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                + "?types=ONLINE&page_number=1";

        driver.get(url);

        scrollToElement(driver, wait);

        WebElement paginationDiv = driver.findElement(
                By.cssSelector("div.mantine-Group-root.mantine-13v5ff3"));
        List<WebElement> pages = paginationDiv.findElements(
                By.cssSelector(
                        "button.mantine-UnstyledButton-root.mantine-Pagination-control.mantine-1gh1g76"));
        int lastPage = Integer.parseInt(pages.getLast().getText());
        this.pageIterator = IntStream.rangeClosed(1, lastPage).iterator();
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

            } catch (TimeoutException e) {
                // 현재 화면에서 요소가 보이지 않는 경우 스크롤
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 10000);");
            }
        }
    }

    private void inflearnCrawlingByOnePage(Integer page, WebDriverWait wait, Platform findPlatform) {
        log.info("페이지: {}",page);
        String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                + "?types=ONLINE&page_number=" + page;
        driverForCourseData.get(url);
        List<CrawlingCourseDto> courseDtoList = new ArrayList<>();
        // 전체 페이지 로드 완료 대기
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));

        // 특정 요소가 표시될 때까지 대기
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("ul.css-sdr7qd.mantine-1avyp1d")
        ));
        WebElement firstUl = driverForCourseData.findElement(By.cssSelector("ul.css-sdr7qd.mantine-1avyp1d"));
        List<WebElement> firstCourses = firstUl.findElements(
                By.cssSelector("li.css-8atqhb.mantine-1avyp1d"));
        log.info("first count: {}", firstCourses.size());

        WebElement secondUrl = driverForCourseData.findElement(By.cssSelector("ul.css-2ldd65.mantine-1avyp1d"));
        List<WebElement> secondCourses = secondUrl.findElements(
                By.cssSelector("li.mantine-1avyp1d"));
        log.info("second count: {}", secondCourses.size());

        for (WebElement course : firstCourses) {
            String baseCourseUrl = course.findElement(By.tagName("a")).getAttribute("href");
            int keywordIndex = baseCourseUrl.indexOf("attributionToken");
            String encodedUrl = (keywordIndex != -1) ? baseCourseUrl.substring(0,
                    baseCourseUrl.lastIndexOf("?", keywordIndex)) : baseCourseUrl;
            String courseUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
            String[] parts = courseUrl.split("/");
            String courseSlug = parts[parts.length - 1];

            String thumbnailImage = null;
            String thumbnailVideo = null;
            try {
                thumbnailImage = course.findElement(
                                By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                        .findElement(By.tagName("img")).getAttribute("src");
            } catch (NoSuchElementException e) {
                log.info("비디오 썸네일");
                thumbnailVideo = course.findElement(
                                By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                        .findElement(By.tagName("source")).getAttribute("src");
            }
            String title = course.findElement(
                    By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22")).getText();
            String teacher = course.findElement(
                    By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth")).getText();

            CrawlingCourseDto courseDto = CrawlingCourseDto.builder()
                    .categorySlug(categorySlug)
                    .platform(findPlatform)
                    .title(title)
                    .courseUrl(courseUrl)
                    .thumbnailImage(thumbnailImage)
                    .thumbnailVideo(thumbnailVideo)
                    .teacher(teacher)
                    .courseSlug(courseSlug)
                    .build();

            courseDtoList.add(courseDto);

        }

        for (WebElement course : secondCourses) {
            String baseCourseUrl = course.findElement(By.tagName("a")).getAttribute("href");
            int keywordIndex = baseCourseUrl.indexOf("attributionToken");
            String encodedUrl = (keywordIndex != -1) ? baseCourseUrl.substring(0,
                    baseCourseUrl.lastIndexOf("?", keywordIndex)) : baseCourseUrl;
            String courseUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
            String[] parts = courseUrl.split("/");
            String courseSlug = parts[parts.length - 1];

            String thumbnailImage = null;
            String thumbnailVideo = null;
            try {
                thumbnailImage = course.findElement(
                                By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                        .findElement(By.tagName("img")).getAttribute("src");
            } catch (NoSuchElementException e) {
                log.info("비디오 썸네일");
                thumbnailVideo = course.findElement(
                                By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"))
                        .findElement(By.tagName("source")).getAttribute("src");
            }
            String title = course.findElement(
                    By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22")).getText();
            String teacher = course.findElement(
                    By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth")).getText();

            CrawlingCourseDto courseDto = CrawlingCourseDto.builder()
                    .categorySlug(categorySlug)
                    .platform(findPlatform)
                    .title(title)
                    .courseUrl(courseUrl)
                    .thumbnailImage(thumbnailImage)
                    .thumbnailVideo(thumbnailVideo)
                    .teacher(teacher)
                    .courseSlug(courseSlug)
                    .build();

            courseDtoList.add(courseDto);
        }
        courseIterator = courseDtoList.iterator();
    }

}
