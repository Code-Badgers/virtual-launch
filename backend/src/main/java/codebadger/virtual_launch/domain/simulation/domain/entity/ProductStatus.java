package codebadger.virtual_launch.domain.simulation.domain.entity;

public enum ProductStatus {
    PLANNING,      // 기획 중 (사양 입력 단계)
    ANALYZING,     // AI 분석 중 (시뮬레이션 가동 중)
    TESTING,       // 가상 테스트 중 (시장 반응 수집 중)
    PRE_LAUNCH,    // 출시 예정 (분석 완료 후 대기)
    LAUNCHED,      // 출시 완료 (최종 리포트 생성 및 완료)
    CANCELED,      // 기획 중단
}
