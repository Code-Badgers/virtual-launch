package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawler;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DanawaSpecCrawler extends DanawaBaseCrawler  implements SpecCrawler {

    private final WebDriverFactory webDriverFactory;
    private SpecCrawlingResultDto specCrawlingResultDto;

    public DanawaSpecCrawler(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public SpecCrawlingResultDto crawlSpecs (String keyword) { //
        WebDriver driver = webDriverFactory.createDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            navigateToProductDetail(driver, keyword); // 부모 로직 호출

            return SpecCrawlingResultDto.builder().build();

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SPEC_CRAWLING_FAILED);
        } finally {
            driver.quit();
        }
    }

    @Override
    public boolean supports (String platform){
        return "danawa".equalsIgnoreCase(platform);
    }
}