package codebadger.virtual_launch.domain.crawling.infrastructure;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawler;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.infrastructure.config.CrawlingProperties;
import codebadger.virtual_launch.domain.crawling.infrastructure.config.CrawlingProperties.SpecConfig;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class DanawaSpecCrawler extends DanawaBaseCrawler  implements SpecCrawler {

    private final WebDriverFactory webDriverFactory;
    private final SpecConfig specConfig;

    public DanawaSpecCrawler(WebDriverFactory webDriverFactory, CrawlingProperties crawlingProperties) {
        this.webDriverFactory = webDriverFactory;
        this.specConfig = crawlingProperties.getDanawa().getSpec();
    }

    @Override
    public List<SpecCrawlingResultDto> crawlSpecs (String keyword, int limit) { //
        WebDriver driver = webDriverFactory.createDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            List<SpecCrawlingResultDto> resultList = new ArrayList<>();

            List<String> productUrls =  navigateToProductDetail(driver, keyword, limit * 2); // 부모 로직 호출 (크롤링 실패를 대비해 *2 진행)
            
            for(String url : productUrls) {
                if (resultList.size() >= limit) { // 크롤링 갯수를 충족하면 루프 종료
                    break;
                }

                try { // 단종된 상품일 경우 크롤링 패스
                    driver.get(url); // 타겟 페이지로 이동

                    boolean isDiscontinued = !driver.findElements(
                            By.xpath("//*[contains(text(), '가격비교 서비스가 종료') or contains(text(), '단종')]")
                    ).isEmpty();

                    if(isDiscontinued) {
                        log.info("단종된 상품입니다. 해당 제품에 대한 크롤링을 건너뜁니다. URL: {}", driver.getCurrentUrl());
                        continue;
                    }

                    // 크롤링이 정상적으로 이루어지는 상품일 경우
                    String modelName = extractModelName(driver); // 제품명
                    BigDecimal currentPrice = extractCurrentPrice(driver); // 현재 가격 (최저가 기준)

                    // 스크롤 및 섹션 활성화
                    scrollToSpecSection(driver, wait, js);

                    Map<String, String> rawSpecs = extractRawSpecs(driver); // 제품 스펙 테이블

                    SpecCrawlingResultDto resultDto =  SpecCrawlingResultDto.builder()
                            .modelName(modelName)
                            .currentPrice(currentPrice)
                            .rawSpecs(rawSpecs)
                            .build();

                    resultList.add(resultDto);
                    log.info("경쟁사 제품 정상 크롤링 완료 (현재 {}/{} 개)", resultList.size(), limit);
                } catch (Exception e) {
                    log.warn("상품 파싱 중 오류 발생. 다음 상품으로 건너뜁니다. URL: {}, 사유: {}", url, e.getMessage());
                    continue;
                }
            }
            return resultList;

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SPEC_CRAWLING_FAILED);
        } finally {
            driver.quit();
        }
    }

    // 제품명 추출
    private String extractModelName(WebDriver driver) {
        String modelNameSelector = specConfig.getModelNameSelector();
        return driver.findElement(By.cssSelector(modelNameSelector)).getText().trim();
    }

    // 현재 가격 (최저가 기준) 추출
    private BigDecimal extractCurrentPrice(WebDriver driver) {
        String priceSelector = specConfig.getPriceSelector();
        String priceText = driver.findElement(By.cssSelector(priceSelector)).getText();
        // 숫자만 남기고 제거
        String numberOnly = priceText.replaceAll("[^0-9]", "");
        return numberOnly.isEmpty() ? BigDecimal.ZERO : new BigDecimal(numberOnly);
    }

    // 스크롤 이동
    private void scrollToSpecSection(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        // 제품 스펙 요소가 나타날 때까지 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("prod_spec")));

        for (int i = 0; i < 20; i++) { // 무한 루프 방지를 위해 최대 20번만 시도
            try {
                // 제품 스펙 테이블이 보이는지 확인
                WebElement specTable = driver.findElement(By.className("prod_spec"));

                if (specTable.isDisplayed()) {
                    // 클릭 이벤트를 위해 해당 요소를 화면 중앙으로 가져옴
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", specTable);
                    break;
                }
            } catch (Exception e) {
                // 아직 못 찾았으면 1000px 더 내리고 0.5초 대기
                js.executeScript("window.scrollBy(0, 1000);");
                Thread.sleep(500);
            }
        }
    }

    // 스펙 테이블 추출
    private Map<String, String> extractRawSpecs(WebDriver driver) {
        Map<String, String> rawSpecs = new HashMap<>();
        String specTableSelector = specConfig.getSpecTableSelector();

        try {
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements specRows = doc.select(specTableSelector);

            for (Element row : specRows.select("tr")) {
                String key = row.select("th").text().trim();
                String value = row.select("td").text().trim();

                // 키와 값이 모두 존재하는 경우에만 맵에 추가
                if (!key.isEmpty() && !value.isEmpty()) {
                    rawSpecs.put(key, value);
                }

            }
        } catch (Exception e) {
            log.error("Jsoup 파싱 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SPEC_CRAWLING_FAILED);
        }
        return rawSpecs;
    }

    @Override
    public boolean supports (String platform){
        return "danawa".equalsIgnoreCase(platform);
    }
}