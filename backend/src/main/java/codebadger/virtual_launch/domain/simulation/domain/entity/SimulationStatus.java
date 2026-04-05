package codebadger.virtual_launch.domain.simulation.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SimulationStatus { //
    PLANNING("기획 중"),      // (기본값) 사양 입력 및 수정 가능 단계
    PROCESSING("분석 중"),    // 시뮬레이션(크롤링 + 매칭) 진행 중 (수정 불가)
    COMPLETED("분석 완료"),   // 시뮬레이션 성공 및 리포트 생성 완료
    FAILED("분석 실패"),      // 비동기 작업 중 오류 발생
    CANCELED("기획 중단");    // 사용자가 기획을 취소한 상태

    private final String description;
}
