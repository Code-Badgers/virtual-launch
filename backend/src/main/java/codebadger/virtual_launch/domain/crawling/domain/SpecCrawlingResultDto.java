package codebadger.virtual_launch.domain.crawling.domain;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpecCrawlingResultDto extends BaseTimeEntity {
    private String brandName; // 브랜드명
    private String modelName; // 경쟁사 제품명
    private BigDecimal releasePrice; // 출시 가격
    private BigDecimal currentPrice; // 현재 가격 (최저가 기준)
    private Map<String, String> rawSpecs; // 경쟁사 제품 원시 데이터
    private String competitorProductImageUrl; // 경쟁사 제품 대표 이미지 URL
    private OffsetDateTime releaseDate; // 경쟁사 제품 출시일

    public boolean isValid() {
        return modelName != null && !modelName.isBlank();
    }
}
