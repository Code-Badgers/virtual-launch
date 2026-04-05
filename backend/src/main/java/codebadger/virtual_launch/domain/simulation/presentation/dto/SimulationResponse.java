package codebadger.virtual_launch.domain.simulation.presentation.dto;

import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;

public record SimulationResponse(
        Long projectId,
        Long productId, // 시뮬레이션된 제품 ID
        SimulationStatus simulationStatus // PROCESSING
) {
}
