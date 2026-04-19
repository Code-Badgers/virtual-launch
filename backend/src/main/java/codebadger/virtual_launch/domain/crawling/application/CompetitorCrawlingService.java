package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.SpecCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.infrastructure.DanawaSpecCrawler;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorCrawlingService {

    private final DanawaSpecCrawler danawaSpecCrawler;
    private final CompetitorProductRepository competitorProductRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CompetitorProductSaver competitorProductSaver;

    @Async
    public void crawlSpecs(String keyword, Long productId, Integer limit) { // 비동기 경쟁사 제품 스펙 크롤링
        // 크롤링할 상세 스펙 갯수 미입력 시 기본값 3
        int targetLimit = (limit != null) ? limit : 3;

        try {
            // 사용자 런칭 예정 제품으로부터 카테고리 조회 (필수 입력 스펙 확인용)
            ProductSpec productSpec = productSpecRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // 루프 외부로 추출하여 중복 탐색 방지
            Map<String, Map<String, RequiredSpec>> categoryTemplate = productSpec.getCategory().getRequiredSpecs();
            Category category = productSpec.getCategory();

            // 크롤링 수행
            List<SpecCrawlingResultDto> dtoList = danawaSpecCrawler.crawlSpecs(keyword, targetLimit);

            competitorProductSaver.saveCrawledProducts(dtoList, category, categoryTemplate);

        } catch (Exception e) {
            log.error("경쟁사 제품 스펙 크롤링 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("크롤링 비동기 트랜잭션 롤백 처리", e);
        }
    }
}
