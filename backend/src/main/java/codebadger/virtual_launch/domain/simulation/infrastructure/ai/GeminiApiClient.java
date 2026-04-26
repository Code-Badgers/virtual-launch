package codebadger.virtual_launch.domain.simulation.infrastructure.ai;

import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.GeminiRequest;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.GeminiResponse;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient { //Gemini API 통신 및 메시지 규격 변환

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String model = "gemini-2.5-flash-lite";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    // Gemini API 무료 티어는 분당 15회 제한 → 안전 마진 포함 4.5초 간격 강제
    private static final long MIN_INTERVAL_MS = 4500;
    private long lastCallTime = 0;

    public synchronized String generateText(String prompt) {
        throttle();

        String geminiUri = String.format("%s%s:generateContent?key=%s", BASE_URL, model, apiKey);
        GeminiRequest request = GeminiRequest.of(prompt);
        long startTime = System.currentTimeMillis();

        try {
            GeminiResponse response = webClient.post()
                    .uri(URI.create(geminiUri))
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    // 재시도 정책 적용
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)) // 최대 3회 재시도, 초기 백오프 2초
                            .maxBackoff(Duration.ofSeconds(20)) // 최대 대기 시간 20초
                            .filter(throwable ->
                                    throwable instanceof WebClientResponseException.TooManyRequests ||
                                            throwable instanceof WebClientResponseException.ServiceUnavailable)
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure())) // 3번 다 실패할 경우 원본 에러 던지기
                    .block(); // 동기 방식으로 응답 대기

            if(response == null || response.getResponseText() == null) {
                log.warn("Gemini API 응답이 비어있습니다");
                return null;
            }

            long elapsed = System.currentTimeMillis() - startTime;
            String responseText = response.getResponseText().trim();
            log.info("Gemini API 호출 성공 - 응답 길이: {}자, 소요 시간: {}ms", responseText.length(), elapsed);

            // 응답에서 텍스트 추출 및 반환
            return response.getResponseText().trim();
        } catch (Exception e) {
            log.error("Gemini API 호출 중 치명적 오류 발생", e);
            return null;
        }
    }

    private void throttle() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastCallTime;
        if (elapsed < MIN_INTERVAL_MS) {
            try {
                Thread.sleep(MIN_INTERVAL_MS - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastCallTime = System.currentTimeMillis();
    }
}
