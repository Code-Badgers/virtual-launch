package codebadger.virtual_launch.domain.simulation.presentation.dto;

import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;

public record MatchResultDto(
        CompetitorProduct product, // 제품 정보 전체
        MatchScoreDto score        // 계산된 점수 상세
) {
}
