package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.infrastructure.DanawaSpecCrawler;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
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
    private final CategoryRepository categoryRepository;

    @Async
    @Transactional
    public void crawlSpecs(String keyword, Long categoryId) { // 비동기 경쟁사 제품 스펙 크롤링

        try {
            // 크롤링 수행
            SpecCrawlingResultDto dto = danawaSpecCrawler.crawlSpecs(keyword);

            // 카테고리 조회 (필수 입력 스펙 확인용)
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            // rawSpec 가공 로직
            Map<String, Map<String, RequiredSpec>> detailedSpecs =
                    processRawSpecs(category.getRequiredSpecs(), dto.getRawSpecs());

            CompetitorProduct competitorProduct = CompetitorProduct.builder()
                    .modelName(dto.getModelName())
                    .currentPrice(dto.getCurrentPrice())
                    .detailedSpecs(detailedSpecs)
                    .lastCrawledAt(OffsetDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("경쟁사 제품 스펙 크롤링 중 오류 발생: {}", e.getMessage());
        }
    }

    // rawSpecs을 카테고리 구조에 맞게 매핑
    private Map<String, Map<String, RequiredSpec>> processRawSpecs(
            Map<String, Map<String, RequiredSpec>> categoryTemplate,
            Map<String, String> rawSpecs) {

        Map<String, Map<String, RequiredSpec>> detailedSpecs = new HashMap<>();

        // 카테고리 템플릿을 순회하면서 rawSpecs에서 값 추출

        return detailedSpecs;
    }
}
