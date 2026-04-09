package codebadger.virtual_launch.domain.crawling.infrastructure;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
public class WebDriverFactory { // 공통 WebDriver 생성 로직
    public WebDriver createDriver() {
        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();

        // 실제 브라우저처럼 보이게 하는 핵심 설정
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        // 추가적인 차단 방지 옵션들
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 제어 흔적 제거
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // 자동화 소프트웨어에 의해 제어되고 있습니다 문구 제거

        return new ChromeDriver(options);
    }
}
