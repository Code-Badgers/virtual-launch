package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationProject;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.SimulationProjectRepository;
import codebadger.virtual_launch.domain.simulation.domain.service.ProductMatcher;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchResultDto;
import codebadger.virtual_launch.domain.simulation.presentation.dto.SimulationRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationService {

    private final ProductMatcher productMatcher;
    private final ReviewCrawlingService reviewCrawlingService;
    private final CategoryRepository categoryRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CompetitorProductRepository competitorProductRepository;
    private final SimulationProjectRepository simulationProjectRepository;

    //   사용자가 런칭하고자 하는 제품과 경쟁사의 제품을 명확히 구분
    @Transactional
    public Long startSimulation(SimulationRequest dto) {
        ProductSpec productSpec = productSpecRepository.findById((dto.productId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 시뮬레이션 상태 업데이트 (영구 변경)
        SimulationProject project = SimulationProject.builder()
                .productSpec(productSpec)
                .projectName(dto.projectName())
                .projectDescription(dto.projectDescription())
                .simulationStatus(SimulationStatus.PROCESSING)
                .build();

        SimulationProject savedProject = simulationProjectRepository.save(project);

        // 경쟁사 제품 데이터 가져오기
        List<CompetitorProduct> competitors = competitorProductRepository.findByCategory(productSpec.getCategory());

        // 제품 스펙과 경쟁사 제품 데이터를 매칭하기 위한 준비 단계 / Map 형태로 변환
        Map<String, Object> targetMap = convertToMap(productSpec);

        List<Map<String, Object>> competitorMaps = competitors.stream()
                .map(comp ->{
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", comp.getCompetitorProductId());
                    map.put("name", comp.getModelName());
                    map.put("price", comp.getReleasePrice());
                    return map;
                })
                .toList();

        // 매칭 결과를 바탕으로 제품ID나 제품명을 ReviewCrawlingService로 넘겨주며 크롤링을 트리거
        List<MatchResultDto> topMatches = productMatcher.findTopMatches(productSpec, dto.competitorCount());

        for(MatchResultDto result : topMatches) {
            String productName = result.product().getModelName(); // 경쟁사 제품명
            reviewCrawlingService.crawlAndSaveReviews(productName);
        }
        return savedProject.getProjectId();
    }

    // 사용자 런칭 스펙을 Map 형태로 변환하는 메서드
    private Map<String, Object> convertToMap(ProductSpec productSpec) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", productSpec.getProductId());
        map.put("name", productSpec.getProductName());
        map.put("price", productSpec.getPlannedPrice());

        return map;
    }
}
