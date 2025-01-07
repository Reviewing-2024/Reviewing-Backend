package com.reviewing.review.crawling;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class FastcampusCrawling {

    @GetMapping("/crawling/fastcampus")
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
            String categorySlug = "category_online_datasciencedeep";
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
