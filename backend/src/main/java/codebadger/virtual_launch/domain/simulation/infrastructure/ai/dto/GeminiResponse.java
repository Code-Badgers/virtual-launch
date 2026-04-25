package codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto;

import java.util.List;

public record GeminiResponse( // 구글 Gemini API의 공식 응답 규격 반환 (외부 통신용)
        List<Candidate> candidates
) {
    public record Candidate(
            Content content
    ) {}

    public record Content(
            List<Part> parts
    ) {}

    public record Part(
            String text
    ) {}

    public String getResponseText() {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0).content().parts().get(0).text();
    }
}
