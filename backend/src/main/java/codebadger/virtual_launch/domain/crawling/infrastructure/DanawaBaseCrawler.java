package codebadger.virtual_launch.domain.crawling.infrastructure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public abstract class DanawaBaseCrawler {

    protected List<String> navigateToProductDetail (WebDriver driver, String keyword, Integer limit) {
        List<String> validUrls = new ArrayList<>();
        // 사용자가 입력한 키워드를 기반으로 검색 결과 url 동적 생성
        String searchUrl = "https://search.danawa.com/dsearch.php?query=" + keyword;
        driver.get(searchUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 현재 검색 창의 ID 저장
        String originalWindow = driver.getWindowHandle();

        // 화면에 있는 모든 상품을 리스트로 가져오기
        List<WebElement> productLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".prod_name a")));

        for (int i = 0; i < productLinks.size(); i++) {
            // limit를 다 채우면 루프 즉시 종료
            if (validUrls.size() >= limit) {
                break;
            }

            try {
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
                    log.info("유효한 상품 URL 수집 완료 ({}/{})", validUrls.size(), limit);
                    validUrls.add(currentUrl);
                } else {
                    log.info("{}번째 상품은 외부 쇼핑몰로 연결되었습니다.", i);
                }
                driver.close();
                driver.switchTo().window(originalWindow);

            } catch (Exception e) {
                log.warn("상품 링크 클릭 중 오류 발생: {}", e.getMessage());
                // 에러가 났을 때도 탭이 열려있다면 닫고 복귀하는 방어적 코드
                if (driver.getWindowHandles().size() > 1) {
                    driver.close();
                    driver.switchTo().window(originalWindow);
                }
            }
        }
        if (validUrls.isEmpty()) {
            log.warn("키워드 '{}'에 대한 유효한 다나와 상품을 찾을 수 없습니다.", keyword);
        }
        return validUrls;
    }
}