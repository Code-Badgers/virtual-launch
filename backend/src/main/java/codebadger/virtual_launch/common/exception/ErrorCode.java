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

    // 잔액
    LACK_OF_BALANCE(HttpStatus.BAD_REQUEST, "W001", "지갑 잔액이 부족합니다."),

    // 리뷰 크롤링
    CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CR001", "리뷰 크롤링에 실패했습니다.");

    private final HttpStatus status;
    private final String code; // 필요 시 title 외에 별도 관리용
    private final String message;
}