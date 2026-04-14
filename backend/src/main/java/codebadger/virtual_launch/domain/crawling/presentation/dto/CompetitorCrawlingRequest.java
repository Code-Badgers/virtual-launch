package codebadger.virtual_launch.domain.crawling.presentation.dto;

public record CompetitorCrawlingRequest(
        String keyword,
        Long productId
) {
}
