package codebadger.virtual_launch.domain.simulation.domain.service;

import org.springframework.stereotype.Component;

@Component
public class ReviewAnalysisPromptGenerator { // 리뷰 분석 프롬프트

    private static final String REVIEW_ANALYSIS_PROMPT_TEMPLATE = """
            당신은 전자제품 시장 조사 및 리뷰 분석 전문가입니다.
            제공된 [리뷰 원본 데이터]를 분석하여 소비자들의 핵심 여론을 정확히 정량화 및 요약하고, 
            마케팅/제품 기획 관점에서 활용 가능한 인사이트를 도출해주세요.

            [리뷰 분석 규칙]
            1. sentiment_score: 전체 리뷰의 감성을 종합하여 0.0(매우 부정)에서 10.0(매우 긍정) 사이의 소수점 첫째 자리 숫자로 도출하세요.
            2. reviewTags: 리뷰에서 가장 많이 언급된 핵심 특징이나 기능 키워드(명사형)를 최대 5개까지 배열로 추출하세요. (예: "가성비", "발열", "디자인") 감정 표현은 제외 (예: "좋음", "별로")
            3. positive_points: 소비자들이 가장 만족해하는 긍정적인 요소들을 3문장 이내로 명확하게 요약하세요.
            4. pain_points: 소비자들이 가장 불편해하거나 개선을 요구하는 치명적인 단점들을 3문장 이내로 요약하세요.
            
            [리뷰 원본 데이터]
            %s
            
            반드시 아래 JSON 형식으로만 대답해. 마크다운(```json) 같은 건 절대 포함하지 마.
            {
              "sentiment_score": 4.5,
              "reviewTags": ["디자인", "발열", "무게", "배터리 타임", "타건감"],
              "positive_points": "세련된 디자인과 가벼운 무게 덕분에 휴대성이 매우 뛰어나다는 점이 가장 큰 장점으로 꼽힙니다. 또한 키보드의 타건감이 우수하여 타이핑 작업에 유리하다는 평가를 받습니다.",
              "pain_points": "가벼운 작업 외에는 심각한 발열과 그로 인한 성능 저하가 발생한다는 치명적인 불만이 있습니다. 배터리 타임이 매우 짧아 실사용에 불편함이 큽니다."
            }
            """;
}
