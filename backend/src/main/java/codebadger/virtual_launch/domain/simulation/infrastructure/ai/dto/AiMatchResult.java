package codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto;

import java.util.Map;

public record AiMatchResult( // AI 모델이 반환 (외부 통신용)
        double totalScore, // 유사도 총점
        String feedback, // AI 종합 분석 (점수 산정 이유)
        Map<String, Double> itemScores // 항목별 세부 점수
) { }
