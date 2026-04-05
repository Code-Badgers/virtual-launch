package codebadger.virtual_launch.domain.simulation.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.simulation.application.SimulationService;
import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationStatus;
import codebadger.virtual_launch.domain.simulation.presentation.dto.SimulationRequest;
import codebadger.virtual_launch.domain.simulation.presentation.dto.SimulationResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping
    @Operation(summary = "제품 시뮬레이션 시작", description = "제품 시뮬레이션을 시작합니다. 시뮬레이션이 완료되면 제품의 상태가 업데이트됩니다.")
    public SuccessResponse startSimulation(@Valid @RequestBody SimulationRequest request) {
        Long projectId = simulationService.startSimulation(request);

        SimulationResponse reponse = new SimulationResponse(
                projectId,
                request.productId(),
                SimulationStatus.PROCESSING
        );

        return SuccessResponse.ok(reponse, "시뮬레이션이 시작되었습니다.\n 시뮬레이션이 완료되면 진행 상태가 업데이트됩니다.");
    }
}
