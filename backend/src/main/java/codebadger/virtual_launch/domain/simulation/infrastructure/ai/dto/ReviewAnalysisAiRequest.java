package codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto;

import java.util.List;

public record ReviewAnalysisAiRequest(
        Long competitorProductId, // 어떤 경쟁사 제품의 리뷰들을 분석할 것인지
        List<ReviewDetail> reviews // 분석할 대상이 되는 원본 리뷰 리스트
) {
    public record ReviewDetail (
        int starRating, // 별점 (1~5)
        String originalContent // 리뷰 원문 텍스트
    ) {}
}
