package codebadger.virtual_launch.domain.simulation.infrastructure.ai;

import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.AiMatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "";

    public AiMatchResult analyzeSimilarity() {
        String prompt = String.format("""
                """);

        return null;
    }
}
