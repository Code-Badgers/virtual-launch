package codebadger.virtual_launch.domain.simulation.domain.service;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.GeminiApiClient;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.AiMatchResult;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchResultDto;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchScoreDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMatcher { // 사용자의 가상 상세 스펙과 경쟁사 스펙이 얼마나 유사한가를 계산

    private final CompetitorProductRepository competitorProductRepository;
    private final GeminiApiClient geminiApiClient;
    private final ObjectMapper objectMapper;
    private final WeightBasedSpecCalculator weightBasedSpecCalculator;
    private final ProductMatchPromptGenerator productMatchPromptGenerator;

    // 중첩된 구조의 JSON 데이터를 평탄화하여 각 항목별로 추출하는 로직
    private Map<String, String> flatten(Map<String, Map<String, RequiredSpec>> detailedSpecs) {
        Map<String, String> flatSpecMap = new HashMap<>(); // 평탄화된 스펙을 저장할 맵

        if(detailedSpecs == null) return flatSpecMap;

        for(Map<String, RequiredSpec> group : detailedSpecs.values()) { // 그룹별로 반복
            for (Map.Entry<String, RequiredSpec> entry : group.entrySet()) { // 그룹에 속한 항목을 하나씩 꺼내기

                String specName = entry.getKey(); // 항목명 (예: "CPU", "RAM")
                RequiredSpec spec = entry.getValue(); // 항목의 상세 정보 (사용자가 입력한 값)

                // 선택 옵션이 있을 경우 선택 옵션 사용 아닐 경우 사용자가 입력한 값을 사용
                String finalValue = (spec.selectedOption() != null)
                        ? spec.selectedOption()
                        : spec.value();

                // 값이 존재할 경우에만 평탄화된 맵에 추가
                if(finalValue != null) {
                    flatSpecMap.put(specName, finalValue);
                }
            }
        }
        return flatSpecMap;
    }

    // 정렬 및 상위 제품 선정 로직
    @Transactional(readOnly = true)
    public List<MatchResultDto> findTopMatches(ProductSpec productSpec, int limit) { // 사용자로부터 몇 개의 경쟁사 제품과 비교할 것인지 입력받음 (기본값 3)
        // 데이터 조회 및 평탄화
        Map<String, String> targetMap = flatten(productSpec.getDetailedSpecs());
        List<CompetitorProduct> competitors = competitorProductRepository.findByCategory(productSpec.getCategory());

        if (competitors == null || competitors.isEmpty()) {
            log.warn("카테고리 '{}'에 해당하는 경쟁사 제품이 없습니다.", productSpec.getCategory().getCategoryName());
            throw new BusinessException(ErrorCode.COMPETITOR_PRODUCT_NOT_FOUND);
        }

        return competitors.stream()
                .map(comp -> { // 리스트에서 하나씩 경쟁사 제품을 꺼내서 유사도 분석 수행
                    try{ // Gemini API를 활용한 유사도 분석
                        // 원본 객체를 JSON 문자열로 변환
                        String targetSpecJson = objectMapper.writeValueAsString(targetMap);
                        String competitorSpecJson = objectMapper.writeValueAsString(flatten(comp.getDetailedSpecs()));

                        // AI 모델에 전달할 프롬프트
                        String prompt = productMatchPromptGenerator.generate(targetSpecJson, competitorSpecJson);

                        AiMatchResult aiMatchResult = geminiApiClient.generateText(prompt, AiMatchResult.class);

                        log.info("AI 유사도 분석 성공 - 경쟁사 제품: {}, 총점: {}, 피드백: {}", comp.getModelName(), aiMatchResult.totalScore(), aiMatchResult.feedback());

                        return new MatchResultDto(comp, new MatchScoreDto(aiMatchResult.totalScore(), aiMatchResult.feedback(), aiMatchResult.itemScores()));

                    } catch(Exception e){
                        log.error("유사도 분석 중 오류 발생: {}\n 수동 계산기로 점수를 산출하겠습니다.", e.getMessage());
                        // AI 통신 실패 시, 수동 계산기를 활용한 점수 산출로 대체
                        MatchScoreDto scoreDto = weightBasedSpecCalculator.calculateIndividualScore(targetMap, flatten(comp.getDetailedSpecs())); // 각 경쟁사 제품과의 점수 계산
                        return new MatchResultDto(comp, scoreDto); // 제품 정보와 점수 정보를 함께
                    }

                    })
                        .filter(result -> result != null)
                        .sorted((a,  b) -> Double.compare(b.score().totalScore(), a.score().totalScore()))
                        .limit(limit) // 점수 순으로 정렬 후 상위 limit개 제품 선정
                        .collect(Collectors.toList());
    }
}
