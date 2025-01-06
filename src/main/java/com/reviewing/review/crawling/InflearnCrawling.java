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
public class InflearnCrawling {

//    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
//    public static String WEB_DRIVER_PATH = "/Users/seoyeon/Downloads/chromedriver-mac-arm64";

    @GetMapping("/crawling/inflearn")
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
            String categorySlug = "back-end";
            for (int page = 1; page <= 1; page++) {
                String url = "https://www.inflearn.com/courses/it-programming/" + categorySlug
                        + "?types=ONLINE&page_number=" + page;

                // 페이지 접속
                driver.get(url);

                // 특정 요소가 로드될 때까지 (최대 30초) 대기
                new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("main.mantine-1avyp1d"))
                );

                WebElement firstUl = driver.findElement(By.cssSelector("ul.css-sdr7qd.mantine-1avyp1d"));
                List<WebElement> firstCourses = firstUl.findElements(By.cssSelector("li.css-8atqhb.mantine-1avyp1d"));
                System.out.println("first count: " + firstCourses.size());

                WebElement secondUrl = driver.findElement(By.cssSelector("ul.css-2ldd65.mantine-1avyp1d"));
                List<WebElement> secondCourses = secondUrl.findElements(By.cssSelector("li.mantine-1avyp1d"));
                System.out.println("second count: " + secondCourses.size());

                for (WebElement course : firstCourses) {
                    WebElement urlTag = course.findElement(By.tagName("a"));
                    String courseUrl = urlTag.getAttribute("href");

                    String courseSlug = "";
                    if (courseUrl != null && !courseUrl.isEmpty()) {
                        String[] parts = courseUrl.split("/");
                        if (parts.length > 0) {
                            String lastPart = parts[parts.length - 1];
                            courseSlug = lastPart.split("\\?")[0];
                        }
                    }

                    WebElement thumbnailDiv = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"));
                    String thumbnailImage = null;
                    String thumbnailVideo = null;
                    // img 태그 검색
                    WebElement imgOrVideo = thumbnailDiv.findElement(By.tagName("img"));
                    if (imgOrVideo != null) {
                        // img 썸네일
                        thumbnailImage = imgOrVideo.getAttribute("src");
                    } else {
                        // 비디오인 경우 source 태그 찾기
                        WebElement sourceTag = thumbnailDiv.findElement(By.tagName("source"));
                        if (sourceTag != null) {
                            thumbnailVideo = sourceTag.getAttribute("src");
                        }
                    }
                    WebElement titleTag = course.findElement(By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22"));
                    String title = titleTag.getText();

                    WebElement teacherTag = course.findElement(By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth"));
                    String teacher = teacherTag.getText();

                    System.out.println(title);
                    System.out.println(courseUrl);
                    System.out.println(courseSlug);
                    System.out.println(thumbnailImage);
                    System.out.println(thumbnailVideo);
                    System.out.println(teacher);
                    System.out.println("----------------------");

                }

                for (WebElement course : secondCourses) {
                    WebElement urlTag = course.findElement(By.tagName("a"));
                    String courseUrl = urlTag.getAttribute("href");

                    String courseSlug = "";
                    if (courseUrl != null && !courseUrl.isEmpty()) {
                        String[] parts = courseUrl.split("/");
                        if (parts.length > 0) {
                            String lastPart = parts[parts.length - 1];
                            courseSlug = lastPart.split("\\?")[0];
                        }
                    }

                    WebElement thumbnailDiv = course.findElement(By.cssSelector("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd"));
                    String thumbnailImage = null;
                    String thumbnailVideo = null;
                    // img 태그 검색
                    WebElement imgOrVideo = thumbnailDiv.findElement(By.tagName("img"));
                    if (imgOrVideo != null) {
                        // img 썸네일
                        thumbnailImage = imgOrVideo.getAttribute("src");
                    } else {
                        // 비디오인 경우 source 태그 찾기
                        WebElement sourceTag = thumbnailDiv.findElement(By.tagName("source"));
                        if (sourceTag != null) {
                            thumbnailVideo = sourceTag.getAttribute("src");
                        }
                    }
                    WebElement titleTag = course.findElement(By.cssSelector("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22"));
                    String title = titleTag.getText();

                    WebElement teacherTag = course.findElement(By.cssSelector("p.mantine-Text-root.css-1r49xhh.mantine-aiouth"));
                    String teacher = teacherTag.getText();

                    System.out.println(title);
                    System.out.println(courseUrl);
                    System.out.println(courseSlug);
                    System.out.println(thumbnailImage);
                    System.out.println(thumbnailVideo);
                    System.out.println(teacher);
                    System.out.println("----------------------");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

    }

}
