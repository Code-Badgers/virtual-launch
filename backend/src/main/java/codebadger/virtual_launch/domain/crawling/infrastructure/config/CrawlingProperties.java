package codebadger.virtual_launch.domain.crawling.infrastructure.config;

import codebadger.virtual_launch.common.config.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-crawling.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "crawling")
@Getter @Setter
public class CrawlingProperties {

    private DanawaConfig danawaConfig = new DanawaConfig();

    @Getter @Setter
    public static class DanawaConfig {
        private ReviewConfig review = new ReviewConfig();
        private SpecConfig spec = new SpecConfig();
    }

    @Getter @Setter
    public static class ReviewConfig {
        private String reviewListSelector; // 리뷰 리스트
        private String reviewContentSelector; // 본문
        private String gradeFilterClass; // 평점
        private String starFilterXpath; // 평점 필터
    }

    @Getter @Setter
    public static class SpecConfig {
        private String modelNameSelector; // 모델명
        private String priceSelector; // 제품 가격
        private String specTableSelector; // 제품 상세 스펙 테이블
    }
}
