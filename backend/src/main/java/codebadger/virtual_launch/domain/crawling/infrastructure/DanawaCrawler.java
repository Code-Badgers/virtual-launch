package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.domain.crawling.domain.CrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.domain.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.ReviewCrawler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class DanawaCrawler implements ReviewCrawler {

    @Override
    public CrawlingResultDto crawlReviews(String keyword) {

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();

        // 실제 브라우저처럼 보이게 하는 핵심 설정
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        // 추가적인 차단 방지 옵션들
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 제어 흔적 제거
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // 자동화 소프트웨어에 의해 제어되고 있습니다 문구 제거
        List<RawReview> reviewList = new ArrayList<>();

        int totalReviewCount = 0;

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

            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean isReviewLoaded = false;

            // 리뷰 요소가 나타날 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("danawa-prodBlog-productOpinion-list-self")));

            // 무한 루프 방지를 위해 최대 20번만 시도
            for (int i = 0; i < 20; i++) {
                try {
                    // '쇼핑몰별 상품평' 컨테이너가 화면에 존재하는지 확인
                    WebElement reviewSection = driver.findElement(By.id("danawa-prodBlog-companyReview-list-self"));
                    if (reviewSection.isDisplayed()) {
                        isReviewLoaded = true;
                        // 클릭 이벤트를 위해 해당 요소를 화면 중앙으로 가져옴
                        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", reviewSection);
                        break; // 찾았으면 스크롤 중단!
                    }
                } catch (Exception e) {
                    // 아직 못 찾았으면 1000px 더 내리고 0.5초 대기
                    js.executeScript("window.scrollBy(0, 1000);");
                    Thread.sleep(500);
                }
            }

            // Jsoup 파싱 시 클래스명 변경
            Document doc = Jsoup.parse(driver.getPageSource());

            // 전체 리뷰 개수 추출 (안전한 이중 체크 로직)
            String totalCountStr = doc.select("#productOpinionTabCount").text().replaceAll("[^0-9]", "");
            if (totalCountStr.isEmpty()) {
                totalCountStr = doc.select("#companyReviewTabCount").text().replaceAll("[^0-9]", "");
            }

            if (!totalCountStr.isEmpty()) {
                totalReviewCount = Integer.parseInt(totalCountStr);
            }

            // 별점별 리뷰 1개씩 수집
            for (int star = 5; star >= 1; star--) {
                try {
                    int targetWidth = star * 20;

                    // 드롭다운 필터를 열기 위해 grade_select 영역을 먼저 클릭
                    WebElement dropDown = driver.findElement(By.className("grade_select"));
                    js.executeScript("arguments[0].click();", dropDown);
                    Thread.sleep(500); // 드롭다운 메뉴가 펼쳐질 때까지 잠시 대기

                    // XPath를 이용한 별점 버튼 타겟팅
                    String xpath = String.format("//div[contains(@class, 'grade_select')]//a[.//span[contains(@class, 'star_mask') and contains(@style, '%d')]]", targetWidth);
                    WebElement starFilterBtn = driver.findElement(By.xpath(xpath));

                    // 별점 버튼 클릭
                    js.executeScript("arguments[0].click();", starFilterBtn);

                    // 필터 적용 후 서버에서 AJAX로 리뷰를 새로 가져올 때까지 대기
                    Thread.sleep(1500);

                    // 리뷰가 새로 로드된 후 페이지 소스를 다시 파싱
                    doc = Jsoup.parse(driver.getPageSource());

                    // .rvw_list 안의 첫 번째 li 추출 (해당 별점에 대한 첫 번째 리뷰 추출)
                    Elements items = doc.select(".rvw_list li");

                    if (!items.isEmpty()) { // 리뷰가 존재할 경우
                        Element firstItem = items.first();

                        // 리뷰 본문 텍스트 추출
                        String content = firstItem.select(".atc").text();

                        if (!content.isEmpty()) {
                            reviewList.add(RawReview.builder()
                                    .platform("danawa")
                                    .originalContent(content)
                                    .starRating(star) // 필터링한 별점을 직접 주입
                                    .build());
                        }
                    } else {
                        System.out.println(star + "점 리뷰는 존재하지 않습니다.");
                    }

                } catch (Exception e) {
                    System.out.println(star + "점 필터 조작 실패: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("크롤링 중 오류 발생: " + e.getMessage());
        } finally {
            driver.quit();
        }

        // 전체 개수와 수집된 리뷰 리스트를 "CrawlingResultDto"라는 상자에 함께 담아서 반환
        return CrawlingResultDto.builder()
                .totalReviewCount(totalReviewCount)
                .reviews(reviewList)
                .build();
    }

    @Override
    public boolean supports(String platform) {
        return "danawa".equalsIgnoreCase(platform);
    }
}