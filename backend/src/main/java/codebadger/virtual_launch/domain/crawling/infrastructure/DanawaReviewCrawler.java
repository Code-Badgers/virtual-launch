package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.ReviewCrawler;
import codebadger.virtual_launch.domain.crawling.domain.ReviewsCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DanawaReviewCrawler extends DanawaBaseCrawler  implements ReviewCrawler {

    private final WebDriverFactory webDriverFactory;

    public DanawaReviewCrawler(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public ReviewsCrawlingResultDto crawlReviews(String keyword) {
        WebDriver driver = webDriverFactory.createDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            navigateToProductDetail(driver, keyword); // 부모 로직 호출

            // 스크롤 및 섹션 활성화
            scrollToReviewSection(driver, wait, js);

            // 전체 개수 파싱 (결과를 변수로 받음)
            int totalReviewCount = parseTotalReviewCount(driver);

            // 별점별 리뷰 수집 (결과를 리스트로 받음)
            List<RawReview> reviewList = collectReviewsByStar(driver, js);

            // 전체 개수와 수집된 리뷰 리스트를 "CrawlingResultDto"라는 상자에 함께 담아서 반환
            return ReviewsCrawlingResultDto.builder()
                    .totalReviewCount(totalReviewCount)
                    .reviews(reviewList)
                    .build();

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.REVIEW_CRAWLING_FAILED);
        } finally {
            driver.quit();
        }
    }

    private void scrollToReviewSection(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        // 무한 루프 방지를 위해 최대 20번만 시도
        boolean isReviewLoaded = false;

        // 리뷰 요소가 나타날 때까지 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("danawa-prodBlog-productOpinion-list-self")));

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
    }

    private int parseTotalReviewCount(WebDriver driver) {
        // 전체 리뷰 개수 추출 (안전한 이중 체크 로직)
        int totalReviewCount = 0;

        // Jsoup 파싱 시 클래스명 변경
        Document doc = Jsoup.parse(driver.getPageSource());

        String totalCountStr = doc.select("#productOpinionTabCount").text().replaceAll("[^0-9]", "");
        if (totalCountStr.isEmpty()) {
            totalCountStr = doc.select("#companyReviewTabCount").text().replaceAll("[^0-9]", "");
        }

        if (!totalCountStr.isEmpty()) {
            totalReviewCount = Integer.parseInt(totalCountStr);
        }

        return totalReviewCount;
    }

    private List<RawReview> collectReviewsByStar(WebDriver driver, JavascriptExecutor js) {
        List<RawReview> localReviewList = new ArrayList<>();

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
                Document doc = Jsoup.parse(driver.getPageSource());

                // .rvw_list 안의 첫 번째 li 추출 (해당 별점에 대한 첫 번째 리뷰 추출)
                Elements items = doc.select(".rvw_list li");

                if (!items.isEmpty()) { // 리뷰가 존재할 경우
                    Element firstItem = items.first();

                    // 리뷰 본문 텍스트 추출
                    String content = firstItem.select(".atc").text();

                    if (!content.isEmpty()) {
                        localReviewList.add(RawReview.builder()
                                .platform("danawa")
                                .originalContent(content)
                                .starRating(star) // 필터링한 별점을 직접 주입
                                .build());
                    }
                } else {
                    log.info("{}점 리뷰가 존재하지 않음", star);
                }

            } catch (Exception e) {
                log.warn("{}점 필터 조작 실패: {}", star, e.getMessage());
            }
        }
        return localReviewList;
    }

    @Override
    public boolean supports (String platform){
        return "danawa".equalsIgnoreCase(platform);
    }
}
