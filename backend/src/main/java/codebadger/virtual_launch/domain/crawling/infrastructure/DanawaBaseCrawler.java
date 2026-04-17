package codebadger.virtual_launch.domain.crawling.infrastructure;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public abstract class DanawaBaseCrawler {

    protected boolean navigateToProductDetail (WebDriver driver, String keyword, Integer limit) {
        // 사용자가 입력한 키워드를 기반으로 검색 결과 url 동적 생성
        String searchUrl = "https://search.danawa.com/dsearch.php?query=" + keyword;
        driver.get(searchUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 현재 검색 창의 ID 저장
        String originalWindow = driver.getWindowHandle();

        // 화면에 있는 모든 상품을 리스트로 가져오기
        List<WebElement> productLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".prod_name a")));

        // 화면에 있는 링크 개수와 limt 중 작은 수 선택
        int actualCrawlCount = Math.min(productLinks.size(), limit);

        for (int i = 0; i < actualCrawlCount; i++) {
            // 각 상품 링크 클릭
            WebElement link = productLinks.get(i);
            link.click();

            // 새 탭이 열릴 때까지 대기 후 제어권 전환
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            for (String windowHandle : driver.getWindowHandles()) {
                if (!originalWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // 외부 사이트 전환이 아닌 다나와 사이트의 상품 상세페이지인지 확인
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.startsWith("https://prod.danawa.com/info/")) {
                return true; // 다나와 상세 페이지로 이동 성공
            } else {
                log.info("{}번째 상품은 외부 쇼핑몰로 연결되었습니다.", i);
                driver.close();
                driver.switchTo().window(originalWindow);
            }
        }
        log.warn("검색된 상품 중 다나와 상세 페이지를 찾을 수 없습니다.");
        return false;
    }
}