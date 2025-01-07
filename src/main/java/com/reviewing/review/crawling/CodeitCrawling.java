package com.reviewing.review.crawling;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class CodeitCrawling {

    @GetMapping("/crawling/codeit")
    public void crawling() {

        ChromeOptions options = new ChromeOptions();
        // User-Agent 설정
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        // 헤드리스 모드
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {
            String categorySlug = "FRONTEND"; // FRONTEND, BACKEND, FULLSTACK
            int count = 0;
            for (int page = 1; page <= 1; page++) {

                String url = "https://www.codeit.kr/explore?page=" + page + "&category=" + categorySlug
                        + "&difficulty=&types=";

//                System.out.println(url);

                driver.get(url);

                // 특정 요소가 로드될 때까지 (최대 30초) 대기
                new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("div.TopicCommonCard_container__w89Rp")
                        )
                );

                WebElement topic = driver.findElement(By.cssSelector("div.TopicList_grid__7bZ8U"));

                List<WebElement> courses = topic.findElements(
                        By.cssSelector("div.TopicCommonCard_container__w89Rp"));

                for (WebElement course : courses) {
                    count++;
                    String title = course.findElement(By.cssSelector("p.TopicCommonCard_title__0KrCI")).getText();
                    String courseUrl = course.findElement(
                            By.cssSelector("a.TopicCommonCard_body__3_gHR")).getAttribute("href");
                    String[] parts = courseUrl.split("/");
                    String courseSlug = parts[parts.length - 1];

                    System.out.println(title);
                    System.out.println(courseUrl);
                    System.out.println(courseSlug);
                    System.out.println("----------------------");

                }
            }

            System.out.println(count);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

    }

}

