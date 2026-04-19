package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CompetitorProductSaver {
    private final CompetitorProductRepository competitorProductRepository;

    @Transactional
    public void saveCrawledProducts(List<SpecCrawlingResultDto> dtoList, Category category, Map<String, Map<String, RequiredSpec>> template) {
        List<CompetitorProduct> competitorProducts = dtoList.stream()
                .map(resultDto -> CompetitorProduct.builder()
                        .category(category)
                        .modelName(resultDto.getModelName())
                        .currentPrice(resultDto.getCurrentPrice())
                        .detailedSpecs(processRawSpecs(template, resultDto.getRawSpecs()))
                        .lastCrawledAt(OffsetDateTime.now())
                        .build())
                .toList();

        competitorProductRepository.saveAll(competitorProducts);
    }

    // rawSpecs을 카테고리 구조에 맞게 매핑
    private Map<String, Map<String, RequiredSpec>> processRawSpecs(
            Map<String, Map<String, RequiredSpec>> categoryTemplate,
            Map<String, String> rawSpecs) {

        Map<String, Map<String, RequiredSpec>> detailedSpecs = new HashMap<>();

        // 카테고리 템플릿을 순회하면서 rawSpecs에서 값 추출
        // 대분류 순회
        for(Map.Entry<String, Map<String, RequiredSpec>> groupEntry : categoryTemplate.entrySet()) {
            String categoryName = groupEntry.getKey();
            Map<String, RequiredSpec> specMap = groupEntry.getValue();

            // 대뷴류별 세부 스펙 매핑 결과 저장할 맵
            Map<String, RequiredSpec> detailedSpecMap = new HashMap<>();

            // 세부 스펙 순회
            for(Map.Entry<String, RequiredSpec> specEntry : specMap.entrySet()) {
                String specName = specEntry.getKey();
                RequiredSpec requiredSpec = specEntry.getValue();

                // 크롤링한 내용이 카테고리 템플릿의 상세 스펙의 Key와 일치하는 경우 값을 꺼내고 없을 경우 정보 없음 반환
                String rawValue = rawSpecs.getOrDefault(specName, "정보 없음");

                // 추후 매핑 로직 개선 필요 유사한 키 매핑(cpu, 코어 종류와 같이 나타내는 이름이 다른 경우), 단위 변환 등
                RequiredSpec detailedSpec = RequiredSpec.builder()
                        .label(requiredSpec.label())
                        .type(requiredSpec.type()) // 입력 폼 형태
                        .options(requiredSpec.options())
                        .value(rawValue) // 크롤링한 값 주입
                        .selectedOption(requiredSpec.selectedOption())
                        .build();

                detailedSpecMap.put(specName, detailedSpec);
            }
            detailedSpecs.put(categoryName, detailedSpecMap);
        }

        return detailedSpecs;
    }
}
