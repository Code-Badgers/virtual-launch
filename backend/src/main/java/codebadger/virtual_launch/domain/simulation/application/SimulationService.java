package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationProject;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.SimulationProjectRepository;
import codebadger.virtual_launch.domain.simulation.domain.service.SimulationTaskExecutor;
import codebadger.virtual_launch.domain.simulation.presentation.dto.SimulationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationService {

    private final ProductSpecRepository productSpecRepository ;
    private final SimulationProjectRepository simulationProjectRepository;
    private final SimulationTaskExecutor simulationTaskExecutor;

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
        Long projectId = savedProject.getProjectId();

        // 비동기 작업 트리거 - 시뮬레이션 분석 시작 (크롤링 + 매칭)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                simulationTaskExecutor.runAsyncAnalysis(projectId, productSpec, dto.competitorCount());
            }
        });

        return savedProject.getProjectId();
    }
}
