package com.reviewing.review.crowling;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InflearnCrowling {

//    private WebDriver driver;
//    private WebElement element;
//
//    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
//    public static String WEB_DRIVER_PATH = "/Users/seoyeon/Downloads/chromedriver-mac-arm64";

    @GetMapping("/crowling")
    public void crowling() {

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

                // 현재 페이지의 HTML 소스 가져오기
                String req = driver.getPageSource();

                // Jsoup으로 파싱
                Document soup = Jsoup.parse(req);

                // Python 코드에서 first, second 로 나눈 것과 동일한 부분
                Element first = soup.selectFirst("ul.css-sdr7qd.mantine-1avyp1d");
                Element second = soup.selectFirst("ul.css-2ldd65.mantine-1avyp1d");

                // 각각 li 요소를 추출
                Elements firstCourses = (first != null) ? first.select("li.css-8atqhb.mantine-1avyp1d") : new Elements();
                Elements secondCourses = (second != null) ? second.select("li.mantine-1avyp1d") : new Elements();

                System.out.println("first count: " + firstCourses.size());
                System.out.println("second count: " + secondCourses.size());

                // 첫 번째 ul 처리
                for (Element course : firstCourses) {
                    // a 태그, 썸네일, title, teacher 추출
                    Element urlTag = course.selectFirst("a");
                    Element thumbnail = course.selectFirst("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd");
                    Element titleTag = course.selectFirst("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22");
                    Element teacherTag = course.selectFirst("p.mantine-Text-root.css-1r49xhh.mantine-aiouth");

                    String courseUrl = (urlTag != null) ? urlTag.attr("href") : "";
                    String courseSlug = "";
                    if (!courseUrl.isEmpty()) {
                        // Python의 split 로직
                        String[] parts = courseUrl.split("/");
                        if (parts.length > 0) {
                            String lastPart = parts[parts.length - 1];
                            courseSlug = lastPart.split("\\?")[0];
                        }
                    }

                    // thumbnail 안에 img가 있는지 확인
                    String thumbnailImage = null;
                    String thumbnailVideo = null;
                    if (thumbnail != null) {
                        Element imgOrVideo = thumbnail.selectFirst("img");
                        if (imgOrVideo != null) {
                            // img
                            thumbnailImage = imgOrVideo.attr("src");
                        } else {
                            // source
                            Element sourceTag = thumbnail.selectFirst("source");
                            if (sourceTag != null) {
                                thumbnailVideo = sourceTag.attr("src");
                            }
                        }
                    }

                    String title = (titleTag != null) ? titleTag.text() : "";
                    String teacher = (teacherTag != null) ? teacherTag.text() : "";

                    // 출력 (Python 코드와 유사한 로직)
                    System.out.println(title);
                    System.out.println(courseUrl);
                    System.out.println(courseSlug);
                    System.out.println(thumbnailImage);
                    System.out.println(thumbnailVideo);
                    System.out.println(teacher);
                    System.out.println("----------------------");
                }

                // 두 번째 ul 처리
                for (Element course : secondCourses) {
                    // a 태그, 썸네일, title, teacher 추출
                    Element urlTag = course.selectFirst("a");
                    Element thumbnail = course.selectFirst("div.mantine-AspectRatio-root.css-10tf8cw.mantine-1w8yksd");
                    Element titleTag = course.selectFirst("p.mantine-Text-root.css-10bh5qj.mantine-b3zn22");
                    Element teacherTag = course.selectFirst("p.mantine-Text-root.css-1r49xhh.mantine-aiouth");

                    String courseUrl = (urlTag != null) ? urlTag.attr("href") : "";
                    String courseSlug = "";
                    if (!courseUrl.isEmpty()) {
                        String[] parts = courseUrl.split("/");
                        if (parts.length > 0) {
                            String lastPart = parts[parts.length - 1];
                            courseSlug = lastPart.split("\\?")[0];
                        }
                    }

                    // thumbnail 안에 img가 있는지 확인
                    String thumbnailImage = null;
                    String thumbnailVideo = null;
                    if (thumbnail != null) {
                        Element imgOrVideo = thumbnail.selectFirst("img");
                        if (imgOrVideo != null) {
                            thumbnailImage = imgOrVideo.attr("src");
                        } else {
                            Element sourceTag = thumbnail.selectFirst("source");
                            if (sourceTag != null) {
                                thumbnailVideo = sourceTag.attr("src");
                            }
                        }
                    }

                    String title = (titleTag != null) ? titleTag.text() : "";
                    String teacher = (teacherTag != null) ? teacherTag.text() : "";

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
