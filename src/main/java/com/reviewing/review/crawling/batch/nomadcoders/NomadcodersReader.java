package com.reviewing.review.crawling.batch.nomadcoders;

import com.reviewing.review.course.entity.Course;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.crawling.repository.PlatformRepository;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

@Slf4j
public class NomadcodersReader implements ItemStreamReader<Course> {

    private WebDriver driver;
    private final List<Course> courseDtoList = new ArrayList<>();
    private Iterator<Course> courseIterator;

    private final PlatformRepository platformRepository;

    public NomadcodersReader(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException, NoSuchElementException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");

        this.driver = new ChromeDriver(options);

        Platform findPlatform = platformRepository.findByName("노마드코더");
        String url = "https://nomadcoders.co/courses";
        driver.get(url);
        Actions actions = new Actions(driver);
        int oldCount = 0;
        int scrollCount = 0;

        while (true) {
            // 현재 아이템 수 확인
            List<WebElement> courses = driver.findElements(By.cssSelector("img.rounded-lg"));
            int newCount = 0;
            for (WebElement img : courses) {
                String src = img.getAttribute("srcset");
                if (src != null && !src.trim().isEmpty()) {
                    newCount++;
                }
            }
            if (newCount == oldCount) {
                break;
            }
            oldCount = newCount;
            actions.scrollByAmount(0, 20000).perform();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.info("노마드코더 sleep 에러");
                throw new RuntimeException(e);
            }
            scrollCount++;
            if (scrollCount > 50) {
                break;
            }
        }
        List<WebElement> courses = driver.findElements(By.cssSelector(
                "div.sc-4a1d66b0-0.cZeVxA.flex.flex-col.relative.rounded-lg.items-center"));


        for (WebElement course : courses) {
            String title = course.findElement(By.cssSelector("h3.text-xl.overflow-hidden")).getText();
            String courseUrl = course.findElement(By.cssSelector("a")).getAttribute("href");
            String[] parts = courseUrl.split("/");
            String courseSlug = parts[parts.length - 1];

            String thumbnail = course.findElement(By.cssSelector("img.rounded-lg"))
                    .getAttribute("src");

            Course courseDto = Course.builder()
                    .platform(findPlatform)
                    .title(title)
                    .url(courseUrl)
                    .thumbnailImage(thumbnail)
                    .thumbnailVideo(null)
                    .teacher(null)
                    .slug(courseSlug)
                    .build();

            this.courseDtoList.add(courseDto);
        }

        courseIterator = courseDtoList.iterator();

    }

    @Override
    public Course read() {
        if (courseIterator.hasNext()) {
            return courseIterator.next();
        } else {
            return null;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        ItemStreamReader.super.update(executionContext);
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
