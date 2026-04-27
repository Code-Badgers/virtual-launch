package codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// 리뷰 분석 결과 JSON 매핑용 DTO (분석된 내용은 raw_review 테이블에 업데이트)
public record ReviewAnalysisAiResponse(

        // 긍/부정 스코어 (0.0 ~ 10.0) - 10점에 가까울수록 긍정
        @JsonProperty("sentiment_score")
        double sentimentScore,

        // 리뷰에서 추출된 키워드
        @JsonProperty("review_tags")
        List<String> reviewTags,

        // AI가 요약한 핵심 만족 사항
        @JsonProperty("positive_points")
        String positivePoints,

        // AI가 요약한 핵심 불만 사항
        @JsonProperty("pain_points")
        String painPoints
) {
}
