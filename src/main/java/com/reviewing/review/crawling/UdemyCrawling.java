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
public class UdemyCrawling {

    @GetMapping("/crawling/udemy")
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
            String categorySlug = "web-development";
            for (int page = 1; page <= 1; page++) {
                String url =
                        "https://www.udemy.com/courses/development/" + categorySlug + "/?p=" + page
                                + "&sort=popularity";
                driver.get(url);

                new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.course-list_card__jWLES"))
                );

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

                    System.out.println(title);
                    System.out.println(courseUrl);
                    System.out.println(courseSlug);
                    System.out.println(thumbnail);
                    System.out.println(teacher);
                    System.out.println("----------------------");
                }
                System.out.println(count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

}
