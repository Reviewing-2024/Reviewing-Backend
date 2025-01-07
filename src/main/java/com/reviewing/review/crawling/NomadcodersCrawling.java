package com.reviewing.review.crawling;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NomadcodersCrawling {

    @GetMapping("/crawling/nomad")
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

                System.out.println("newCount: "+newCount);

                // 새 아이템이 안 늘어나면 종료
                if (newCount == oldCount) {
                    break;
                }
                oldCount = newCount;

                // 스크롤 1000px씩
                actions.scrollByAmount(0, 1000).perform();

                // 로딩 대기
                Thread.sleep(4000);

                scrollCount++;
                if (scrollCount > 50) { // 최대 20번 스크롤
                    break;
                }
            }

            List<WebElement> courses = driver.findElements(By.cssSelector(
                    "div.sc-4a1d66b0-0.cZeVxA.flex.flex-col.relative.rounded-lg.items-center"));

            int count = 0;
            for (WebElement course : courses) {
                count++;
                String title = course.findElement(By.cssSelector("h3.text-xl.overflow-hidden")).getText();
                String courseUrl = course.findElement(By.cssSelector("a")).getAttribute("href");
                String[] parts = courseUrl.split("/");
                String courseSlug = parts[parts.length - 1];

                String thumbnail = course.findElement(By.cssSelector("img.rounded-lg"))
                        .getAttribute("src");

                System.out.println(title);
                System.out.println(courseUrl);
                System.out.println(courseSlug);
                System.out.println(thumbnail);
                System.out.println("----------------------");
            }
            System.out.println(count);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

    }

}
