package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.domain.crawling.domain.ReviewCrawler;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public abstract class DanawaBaseCrawler implements ReviewCrawler {

    protected void navigateToProductDetail (WebDriver driver, String keyword){
        // 사용자가 입력한 키워드를 기반으로 검색 결과 url 동적 생성
        String searchUrl = "https://search.danawa.com/dsearch.php?query=" + keyword;
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
    }

    @Override
    public boolean supports (String platform){
        return "danawa".equalsIgnoreCase(platform);
    }
}