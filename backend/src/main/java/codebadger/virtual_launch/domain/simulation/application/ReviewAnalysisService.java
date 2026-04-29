package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.simulation.domain.service.ReviewAnalysisPromptGenerator;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.GeminiApiClient;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.ReviewAnalysisAiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final RawReviewRepository rawReviewRepository;
    private final GeminiApiClient geminiApiClient;
    private final ReviewAnalysisPromptGenerator promptGenerator;

    public void analyzeAndSaveReviews(Long competitorProductId) {

        List<RawReview> rawReviews = rawReviewRepository.findByCompetitorProduct_CompetitorProductId(competitorProductId);

        // 텍스트 가공
        String formattedReviews = rawReviews.stream()
                .map(review -> String.format("(별점: %d) %s", review.getStarRating(), review.getOriginalContent()))
                .collect(Collectors.joining("\n"));

        // AI 프롬프트 생성
        String prompt = promptGenerator.generate(formattedReviews);

        ReviewAnalysisAiResponse response = geminiApiClient.generateText(prompt, ReviewAnalysisAiResponse.class);

        // AI 분석 결과 RawReview 엔터티에 업데이트
        for (RawReview review : rawReviews) {
            review.updateAnalysisResult(
                    response.sentimentScore(),
                    String.join(", ", response.reviewTags()), //List<String>를 String으로 변환하여 저장
                    response.positivePoints(),
                    response.painPoints()
            );
        }

        rawReviewRepository.saveAll(rawReviews);
    }

}
