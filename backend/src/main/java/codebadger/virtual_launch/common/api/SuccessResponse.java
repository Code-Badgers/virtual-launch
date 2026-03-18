package codebadger.virtual_launch.common.api;

import java.time.LocalDateTime;

public record SuccessResponse<T>(
        T data,                 // 실제 결과 데이터
        String message,         // 성공 메시지 (예: "조회 성공", "가입 완료")
        LocalDateTime timestamp // 응답 생성 시각
) {

    public static <T> SuccessResponse<T> ok(T data) {
        return new SuccessResponse<>(data, "요청이 성공적으로 처리되었습니다.", LocalDateTime.now());
    }

    public static <T> SuccessResponse<T> ok(T data, String message) {
        return new SuccessResponse<>(data, message, LocalDateTime.now());
    }
}