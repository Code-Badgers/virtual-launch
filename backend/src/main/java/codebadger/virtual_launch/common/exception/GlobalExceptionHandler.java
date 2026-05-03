package codebadger.virtual_launch.common.exception;

import codebadger.virtual_launch.common.api.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    /**
     * DTO 입력값 검증(@Valid) 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        // 1. 발생한 에러들 중 첫 번째 에러 메시지를 가져옵니다.
        String firstErrorMessage = e.getBindingResult()
                .getAllErrors()
                .getFirst()
                .getDefaultMessage();

        log.error("[MethodArgumentNotValidException] Message: {}, Path: {}",
                firstErrorMessage, request.getRequestURI());

        // 2. 미리 정의된 ErrorCode(예: INVALID_INPUT_VALUE)를 사용하거나 새로 정의합니다.
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse response = new ErrorResponse(
                errorCode.name(),
                errorCode.getStatus().value(),
                firstErrorMessage, // DTO에 적어둔 메시지가 여기에 들어갑니다!
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * JSON 파싱 및 Enum 변환 실패 처리
     * 클라이언트가 Enum에 없는 값을 보냈을 때 발생합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.error("[HttpMessageNotReadableException] Message: {}, Path: {}",
                e.getMessage(), request.getRequestURI());

        String detailMessage = "요청 본문을 읽을 수 없습니다. 형식이 올바른지 확인해주세요.";

        // Enum 변환 실패(InvalidFormatException)인 경우 어떤 필드인지 구체적으로 추출
        if (e.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException prevException) {
            String fieldName = prevException.getPath().isEmpty() ? "unknown" : prevException.getPath().get(0).getFieldName();
            detailMessage = String.format("'%s' 필드의 값이 유효하지 않습니다. 허용된 값을 확인해주세요.", fieldName);
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse response = new ErrorResponse(
                errorCode.name(),               // title (예: INVALID_INPUT_VALUE)
                errorCode.getStatus().value(),  // status (예: 400)
                detailMessage,                  // detail (우리가 가공한 메시지)
                request.getRequestURI(),        // instance (요청 경로)
                LocalDateTime.now()             // timestamp
        );

        return new ResponseEntity<>(response, errorCode.getStatus());
    }


}