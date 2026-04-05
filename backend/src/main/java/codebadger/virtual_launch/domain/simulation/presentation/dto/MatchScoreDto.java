package codebadger.virtual_launch.domain.simulation.presentation.dto;

import java.util.Map;

public record MatchScoreDto(
        double totalScore,           // 최종 평균 점수
        Map<String, Double> eachScores // 항목별 상세 점수
) { }
