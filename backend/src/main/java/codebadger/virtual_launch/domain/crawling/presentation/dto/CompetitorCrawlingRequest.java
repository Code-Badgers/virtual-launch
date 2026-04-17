package codebadger.virtual_launch.domain.crawling.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CompetitorCrawlingRequest(
        String keyword,
        Long productId,

        @Min(value = 1, message = "최소 1개 이상 크롤링 요청 필요")
        @Max(value = 10, message = "경쟁사 제품은 한 번에 최대 10개까지만 크롤링 가능")
        Integer limit // 크롤링 할 경쟁사 제품의 수 (기본값: 3, 최대값: 10)
) {
}
