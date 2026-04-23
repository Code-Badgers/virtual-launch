package codebadger.virtual_launch.domain.simulation.presentation.dto;

import java.util.Map;

public record MatchScoreDto( // 유사도 최종 결과(화면 반환용)
        double totalScore, // 최종 평균 점수
        String feedback, // AI 종합 분석 (점수 산정 이유)
        Map<String, Double> eachScores // 항목별 상세 점수
) { }
