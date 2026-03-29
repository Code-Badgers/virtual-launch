package codebadger.virtual_launch.domain.simulation.domain.service;

import static codebadger.virtual_launch.common.exception.ErrorCode.PRODUCT_NOT_FOUND;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 항목별 점수 산출 로직 - 각각의 항목(가격, CPU, 용량 등)이 사용자의 요구사항과 얼마나 닮았는지를 0점에서 100점 사이의 수치로 환산

    // 어떤 항목에 가중치를 얼마나 줄 것인지

    // 정렬 및 최종 선정
}
