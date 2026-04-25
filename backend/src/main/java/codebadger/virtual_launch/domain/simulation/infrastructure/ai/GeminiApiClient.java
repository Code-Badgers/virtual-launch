package codebadger.virtual_launch.domain.simulation.infrastructure.ai;

import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.GeminiRequest;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient { //Gemini API 통신 및 메시지 규격 변환

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String model = "gemini-1.5-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    public String generateText(String prompt) {
        String geminiUri = String.format("%s%s:generateContent?key=%s", BASE_URL, model, apiKey);
        GeminiRequest request = GeminiRequest.of(prompt);

        try {
            GeminiResponse response = webClient.post()
                    .uri(geminiUri)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block(); // 동기 방식으로 응답 대기

            if(response == null || response.getResponseText() == null) {
                log.warn("Gemini API 응답이 비어있습니다");
                return null;
            }
            // 응답에서 텍스트 추출 및 반환
            return response.getResponseText().trim();
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}
