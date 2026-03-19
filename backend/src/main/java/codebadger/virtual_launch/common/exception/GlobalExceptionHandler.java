package codebadger.virtual_launch.common.exception;

import codebadger.virtual_launch.common.api.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[BusinessException] Code: {}, Message: {}, Path: {}",
                errorCode.name(), e.getMessage(), request.getRequestURI());

        ErrorResponse response = new ErrorResponse(
                errorCode.name(),               // title
                errorCode.getStatus().value(),  // status
                e.getMessage(),                 // detail
                request.getRequestURI(),        // instance (요청 경로 추출)
                LocalDateTime.now()             // timestamp
        );

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * 예상치 못한 최상위 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("[Unhandled Exception] Message: {}, Path: {}", e.getMessage(), request.getRequestURI(), e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(
                errorCode.name(),
                errorCode.getStatus().value(),
                "서버 내부에서 알 수 없는 오류가 발생했습니다.",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}