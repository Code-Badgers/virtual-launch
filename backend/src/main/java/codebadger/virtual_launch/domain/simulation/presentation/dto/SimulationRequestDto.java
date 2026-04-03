package codebadger.virtual_launch.domain.simulation.presentation.dto;

public record SimulationRequestDto(
        Long categoryId,
        Long productId,
        int competitorCount, // 경쟁사 제품 수 (기본값 3)
        boolean useAsync // 비동기 방식으로 시뮬레이션 실행 여부
) {
}
