package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.domain.crawling.domain.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.ReviewCrawler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class DanawaCrawler implements ReviewCrawler {

    @Override
    public List<RawReview> crawlReviews(String keyword) {

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();

        // 실제 브라우저처럼 보이게 하는 핵심 설정
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        // 추가적인 차단 방지 옵션들
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 제어 흔적 제거
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // 자동화 소프트웨어에 의해 제어되고 있습니다 문구 제거
        List<RawReview> reviewList = new ArrayList<>();

        // 사용자가 입력한 키워드를 기반으로 검색 결과 url 동적 생성
        String searchUrl = "https://search.danawa.com/dsearch.php?query=" + keyword;
        // 옵션을 적용하여 드라이버 생성
        WebDriver driver = new ChromeDriver(options);

        try{
            driver.get(searchUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 현재 검색 창의 ID 저장
            String originalWindow = driver.getWindowHandle();

            // 첫 번째 상품 클릭
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".prod_name a"))).click();

            // 새 탭이 열릴 때까지 대기 후 제어권 전환
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            for (String windowHandle : driver.getWindowHandles()) {
                if (!originalWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // 리뷰 영역까지 스크롤
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");

            // 리뷰 요소가 나타날 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("danawa-prodBlog-productOpinion-list-self")));

            // Jsoup 파싱 시 클래스명 변경
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements reviewElements = doc.select("#danawa-prodBlog-productOpinion-list-self .atc");

            for (int i = 0; i < reviewElements.size(); i++) {
                String content = reviewElements.get(i).text();
                // int starRating = Integer.parseInt(starElements.get(i).text().replaceAll("[^0-9]", ""));
                RawReview review = RawReview.builder()
                        .platform("danawa")
                        .originalContent(content)
                        // .starRating(starRating)
                        .build();
                reviewList.add(review);
            }
        } finally { // 브라우저 종료
            driver.quit();
        }
        return reviewList;
    }

    @Override
    public boolean supports(String platform) {
        return "danawa".equalsIgnoreCase(platform);
    }
}
