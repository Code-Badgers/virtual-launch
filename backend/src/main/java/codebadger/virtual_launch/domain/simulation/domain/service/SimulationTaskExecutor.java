package codebadger.virtual_launch.domain.simulation.domain.service;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.simulation.domain.entity.MatchedCompetitor;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationProject;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;
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

    @Async
    @Transactional
    public void runAsyncAnalysis(Long projectId, ProductSpec productSpec, int limit) {
        SimulationProject project = simulationProjectRepository.findById(projectId).orElseThrow();

        try {
            // 유사도 분석
            List<MatchResultDto> topMatches = productMatcher.findTopMatches(productSpec, limit);

            // 순위 부여
            for(int i = 0; i < topMatches.size(); i++) {
                MatchResultDto result = topMatches.get(i);
                saveMatchedCompetitor(project, result, i + 1);

                // 리뷰 크롤링 트리거
                reviewCrawlingService.crawlAndSaveReviews(result.product().getModelName());
            }
            project.updateStatus(SimulationStatus.COMPLETED);
        } catch (Exception e) {
            project.updateStatus(SimulationStatus.FAILED);

            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

    }

    // 사용자 런칭 스펙을 Map 형태로 변환하는 메서드
    private void saveMatchedCompetitor(SimulationProject project,  MatchResultDto dto, int rank) {
        MatchedCompetitor matched = MatchedCompetitor.builder()
                .simulationProject(project)
                .competitorProduct(dto.product())
                .similarityScore(dto.score().totalScore())
                .matchRank(rank)
                .build();
        matchedCompetitorRepository.save(matched);
    }

}
