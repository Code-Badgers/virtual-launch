package codebadger.virtual_launch.domain.simulation.domain.service;

import static codebadger.virtual_launch.common.exception.ErrorCode.PRODUCT_NOT_FOUND;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchResultDto;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchScoreDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMatcher { // 사용자의 가상 상세 스펙과 경쟁사 스펙이 얼마나 유사한가를 계산

    private final CategoryRepository categoryRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CompetitorProductRepository competitorProductRepository;

    // 데이터 필터링 - 카테고리 ID가 일치하는 제품들 (productSpec의 카테고리와 competitorProduct의 카테고리가 일치하는 제품들)
    @Transactional(readOnly = true)
    public List<CompetitorProduct> findBaseMatch(ProductSpec productSpec) {
        ProductSpec foundSpec = productSpecRepository.findById(productSpec.getProductId())
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND));

        Category category = foundSpec.getCategory();
        List<CompetitorProduct> competitorProducts = competitorProductRepository.findByCategory(category);

        return competitorProducts;
    }


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
                flatSpecMap.put(specName, finalValue);

                // 값이 존재할 경우에만 평탄화된 맵에 추가
                if(finalValue != null) {
                    flatSpecMap.put(specName, finalValue);
                }
            }
        }

        return flatSpecMap;
    }

    @Transactional
    public List<Map<String, String>> flattenSpecs(Category category) { // 카테고리가 일치하는 경쟁사 제품들의 스펙을 평탄화하여 추출하는 메서드
        List<CompetitorProduct> competitorProducts = competitorProductRepository.findByCategory(category);

        return competitorProducts.stream()
                .map(product -> flatten(product.getDetailedSpecs()))
                .collect(Collectors.toList()); // 실제로는 평탄화된 제품 리스트 반환
    }

    // 어떤 항목에 가중치를 얼마나 줄 것인지
    public Map<String, Double> getWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("가격", 0.3); // 가격에 30% 가중치

        // 가중치의 총합은 1.0이여야 한다
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if(Math.abs(totalWeight - 1.0) > 0.0001) { // 오차 범위 0.0001 허용
            throw new BusinessException(ErrorCode.INVALID_WEIGHT_SUM);
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

        // 최종 점수 및 상세 내역 dto로 반환
        return new MatchScoreDto(finalScore, eachScores);
    }

    // 정렬 및 상위 3개의 제품 선정
    @Transactional(readOnly = true)
    public List<MatchResultDto> findTopMatches(ProductSpec productSpec, int limit) { // 사용자로부터 몇 개의 경쟁사 제품과 비교할 것인지 입력받음 (기본값 3)
        // 데이터 조회 및 평탄화
        Map<String, String> targetMap = flatten(productSpec.getDetailedSpecs());
        List<CompetitorProduct> competitors = competitorProductRepository.findByCategory(productSpec.getCategory());

        return competitors.stream()
                .map(comp -> {
                    MatchScoreDto scoreDto = calculateIndividualScore(targetMap, flatten(comp.getDetailedSpecs())); // 각 경쟁사 제품과의 점수 계산
                    return new MatchResultDto(comp, scoreDto); // 제품 정보와 점수 정보를 함께
                })
                .sorted((a,  b) -> Double.compare(b.score().totalScore(), a.score().totalScore()))
                .limit(3) // 상위 3개 제품 선정
                .collect(Collectors.toList());
    }
}
