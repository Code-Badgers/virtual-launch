package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.infrastructure.DanawaSpecCrawler;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorCrawlingService {

    private final DanawaSpecCrawler danawaSpecCrawler;
    private final CompetitorProductRepository competitorProductRepository;
    private final ProductSpecRepository productSpecRepository;

    @Async
    @Transactional
    public void crawlSpecs(String keyword, Long productId) { // 비동기 경쟁사 제품 스펙 크롤링

        try {
            // 사용자 런칭 예정 제품으로부터 카테고리 조회 (필수 입력 스펙 확인용)
            ProductSpec productSpec = productSpecRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            Long categoryId = productSpec.getCategory().getCategoryId();

            // 크롤링 수행
            SpecCrawlingResultDto dto = danawaSpecCrawler.crawlSpecs(keyword);

            // rawSpec 가공 로직
            Map<String, Map<String, RequiredSpec>> detailedSpecs =
                    processRawSpecs(productSpec.getCategory().getRequiredSpecs(), dto.getRawSpecs());

            CompetitorProduct competitorProduct = CompetitorProduct.builder()
                    .category(productSpec.getCategory())
                    .modelName(dto.getModelName())
                    .currentPrice(dto.getCurrentPrice())
                    .detailedSpecs(detailedSpecs)
                    .lastCrawledAt(OffsetDateTime.now())
                    .build();

            competitorProductRepository.save(competitorProduct);

        } catch (Exception e) {
            log.error("경쟁사 제품 스펙 크롤링 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("크롤링 비동기 트랜잭션 롤백 처리", e);
        }
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
