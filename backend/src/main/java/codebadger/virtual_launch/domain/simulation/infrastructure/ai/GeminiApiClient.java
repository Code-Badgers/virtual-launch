package codebadger.virtual_launch.domain.simulation.infrastructure.ai;

import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.AiMatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "";

    // 경쟁사 제품 유사도 분석
    public AiMatchResult analyzeSimilarity(String targetSpecJson, String competitorSpecJson) {
        String prompt = String.format("""
        당신은 전자제품 스펙을 비교하는 전문 분석가입니다.
        아래 제공된 사용자 런칭 예정 제품 스펙과 경쟁사 제품 스펙을 비교하여 항목별 유사도를 바탕으로 종합 유사도 점수를 계산해주세요.
        유사도는 0에서 100 사이의 점수로 표현되며, 
        100에 가까울수록 두 제품이 매우 유사하다는 것을 의미합니다.
        
        [분석 및 가중치 산정 규칙]
        1. 각 스펙 항목마다 중요도에 따라 당신(AI)이 스스로 가중치를 판단하여 부여하세요.
        2. 소비자가 가장 중요하게 생각하는 '가격' 항목의 가중치는 반드시 0.3(30%%)으로 고정하세요.
        3. 모든 항목의 가중치 합은 정확히 1.0(100%%)이 되어야 합니다.
        4. 항목별 유사도(0~100점)에 가중치를 곱하여 합산한 값이 총점(totalScore)이 됩니다.
        5. itemScores의 Key값은 임의로 지어내지 말고, 내가 제공한 [사용자 런칭 예정 제품] JSON 데이터에 있는 원본 스펙 항목명을 그대로 1:1로 매핑하여 작성하세요.

        [사용자 런칭 예정 제품]
        %s
        
        [경쟁사 제품]
        %s
        
        반드시 아래 JSON 형식으로만 대답해. 마크다운(```json) 같은 건 절대 포함하지 마.
        {
          "totalScore": 85.5,
          "feedback": "가격 가중치 0.3, CPU 가중치 0.4, RAM 가중치 0.3을 적용했습니다. CPU 성능은 동일하나 RAM 용량에서 차이가 발생하여 감점되었습니다.",
          "itemScores": { "CPU": 90.0, "RAM": 50.0 }
        }
        """, targetSpecJson, competitorSpecJson);

        // WebClient를 통한 비동기 API 호출
        try {
            return null;
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}
