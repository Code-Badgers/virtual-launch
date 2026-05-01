package codebadger.virtual_launch.domain.simulation.domain.service;

import codebadger.virtual_launch.domain.crawling.application.CompetitorCrawlingService;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.simulation.application.ReviewAnalysisService;
import codebadger.virtual_launch.domain.simulation.domain.entity.MatchedCompetitor;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationProject;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.MatchedCompetitorRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.SimulationProjectRepository;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchResultDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimulationTaskExecutor {

    private final ProductMatcher productMatcher;
    private final ReviewCrawlingService reviewCrawlingService;
    private final MatchedCompetitorRepository matchedCompetitorRepository;
    private final SimulationProjectRepository simulationProjectRepository;
    private final CompetitorCrawlingService competitorCrawlingService;
    private final CompetitorProductRepository competitorProductRepository;
    private final ReviewAnalysisService reviewAnalysisService;

    @Async
    @Transactional
    public void runAsyncAnalysis(Long projectId, ProductSpec productSpec, int limit) {
        log.info("시뮬레이션 분석이 시작되었습니다. 프로젝트 ID: {}", projectId);
        SimulationProject project = simulationProjectRepository.findById(projectId).orElseThrow();

        try {
            // 사용자 런칭 제품에 대한 동일 카테고리 경쟁사 제품이 존재하는지 확인
            boolean existsCompetitors = competitorProductRepository.existsByCategory(productSpec.getCategory());

            if(!existsCompetitors) { // 경쟁사 제품이 존재하지 않는 경우
                log.info("동일 카테고리의 경쟁사 제품이 존재하지 않습니다. 카테고리: {}", productSpec.getCategory().getCategoryName());
                // 경쟁사 제품 크롤링
                competitorCrawlingService.crawlSpecs(productSpec.getCategory().getCategoryName(), productSpec.getProductId(), limit);
            }

            // 유사도 분석
            List<MatchResultDto> topMatches = productMatcher.findTopMatches(productSpec, limit);

            // 순위 부여
            for(int i = 0; i < topMatches.size(); i++) {
                MatchResultDto result = topMatches.get(i);
                saveMatchedCompetitor(project, result, i + 1);

                // 리뷰 크롤링 트리거
                reviewCrawlingService.crawlReviews(result.product().getModelName(), result.product().getCompetitorProductId(), limit)
                        .thenRun(() -> { // 리뷰 크롤링이 완료된 후 실행되는 콜백
                            // 리뷰 분석 트리거 (크롤링된 경쟁사 제품 리뷰에 대한 AI 분석)
                            reviewAnalysisService.analyzeAndSaveReviews(result.product().getCompetitorProductId());
                        })
                        .exceptionally(ex -> {
                            log.error("리뷰 크롤링 중 에러 발생: {}", ex.getMessage());
                            return null;

                        });
            }
            project.updateStatus(SimulationStatus.COMPLETED);
            log.info("시뮬레이션 분석이 성공적으로 완료되었습니다. 프로젝트 ID: {}", projectId);
        } catch (Exception e) {
            project.updateStatus(SimulationStatus.FAILED);
            log.error("시뮬레이션 분석 중 오류 발생 : {}", e.getMessage());
        }

    }

    // 사용자 런칭 스펙을 Map 형태로 변환하는 메서드
    private void saveMatchedCompetitor(SimulationProject project,  MatchResultDto dto, int rank) {
        MatchedCompetitor matched = MatchedCompetitor.builder()
                .simulationProject(project)
                .competitorProduct(dto.product())
                .similarityScore(dto.score().totalScore())
                .matchRank(rank)
                .feedback(dto.score().feedback())
                .itemScores(dto.score().eachScores())
                .build();
        matchedCompetitorRepository.save(matched);
    }

}
