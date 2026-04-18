package codebadger.virtual_launch.domain.crawling.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewCrawlingRequest (
        String keyword,
        Long competitorProductId,

        @Min(value = 5, message = "최소 5개 이상 크롤링 요청 필요")
        @Max(value = 50, message = "리뷰 크롤링은 한 번에 최대 50개까지만 크롤링 가능")
        Integer limit
) {}
