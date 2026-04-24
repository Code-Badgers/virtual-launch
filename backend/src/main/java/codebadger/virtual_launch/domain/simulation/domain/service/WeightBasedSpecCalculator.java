package codebadger.virtual_launch.domain.simulation.domain.service;

import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchScoreDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeightBasedSpecCalculator { // 가중치 기반 점수 산출 서비스

    // 어떤 항목에 가중치를 얼마나 줄 것인지
    public Map<String, Double> getWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("가격", 0.3); // 가격에 30% 가중치

        // 가중치의 총합은 1.0이여야 한다
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if(Math.abs(totalWeight - 1.0) > 0.0001) { // 오차 범위 0.0001 허용
            log.warn("가중치 합계가 1.0이 되도록 변경해주시오 :{}", totalWeight);
        }
        return weights;
    }

    // 항목별 점수 산출 로직 - 각각의 항목(가격, CPU, 용량 등)이 사용자의 요구사항과 얼마나 닮았는지를 0점에서 100점 사이의 수치로 환산
    public MatchScoreDto calculateIndividualScore(Map<String, String> targetMap, Map<String, String> compMap) { // 평탄화를 진행한 사용자 스펙과 경쟁사 각각의 제품 스펙
        Map<String, Double> eachScores = new HashMap<>(); // 항목별 점수를 저장
        double weightedScoreSum = 0.0; // (점수 * 가중치)의 총합
        double weightSum = 0.0; // 가중치들의 총합

        // 가중치 기준 가져오기
        Map<String, Double> weights = getWeights();

        for(String key : targetMap.keySet()) { // 사용자 스펙의 각 항목에 대해 반복
            String targetValue = targetMap.get(key);
            String compValue = compMap.get(key);

            double eachScore = 0.0;
            if(compValue != null) { // 값 비교 로직
                eachScore = Objects.equals(targetValue, compValue) ? 100.0 : 0.0; // 일치하면 100점 아닐 경우 0점 (AI를 통한 정교한 점수 로직 추후 구현 고려)
            }

            double weight = weights.getOrDefault(key, 0.1); // 가중치 적용 (없을 경우 기본 0.1)

            eachScores.put(key, eachScore); // 각 항목별 점수 저장
            weightedScoreSum += (eachScore * weight); // 총 점수 합산
            weightSum += weight;
        }

        // 최종 평균 점수 계산
        double finalScore = (weightSum > 0) ? weightedScoreSum / weightSum : 0.0;

        String feedback = "현재 AI 기반 유사도 분석 알고리즘이 연동 중입니다.";

        // 최종 점수 및 상세 내역 dto로 반환
        return new MatchScoreDto(finalScore, feedback, eachScores);
    }
}
