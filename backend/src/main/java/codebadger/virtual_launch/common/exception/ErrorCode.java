package codebadger.virtual_launch.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 사용 중인 이메일입니다."),

    // 지갑
    LACK_OF_BALANCE(HttpStatus.BAD_REQUEST, "W001", "지갑 잔액이 부족합니다."),
    INVALID_CREDIT_VALUE(HttpStatus.BAD_REQUEST, "W002", "유효하지 않은 금액입니다."),
    CONCURRENCY_ERROR(HttpStatus.CONFLICT,"WOO3", "이미 수정된 데이터입니다. 다시 시도해 주세요." ),

    // 크롤링
    REVIEW_CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CR001", "리뷰 크롤링에 실패했습니다."),
    SPEC_CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CR002", "경쟁사 상세 스펙 크롤링에 실패했습니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "존재하지 않는 카테고리입니다."),

    // 런칭 예정 제품 상세 스펙
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "S002", "존재하지 않는 제품입니다."),
    INVALID_WEIGHT_SUM(HttpStatus.BAD_REQUEST, "S003", "가중치의 합은 1.0이어야 합니다."),

    // 경쟁사 제품
    COMPETITOR_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "S004", "존재하지 않는 경쟁사 제품입니다."),

    // 시뮬레이션
    SIMULATION_NOT_FOUND(HttpStatus.NOT_FOUND, "S005", "존재하지 않는 시뮬레이션 프로젝트입니다.");

    private final HttpStatus status;
    private final String code; // 필요 시 title 외에 별도 관리용
    private final String message;
}