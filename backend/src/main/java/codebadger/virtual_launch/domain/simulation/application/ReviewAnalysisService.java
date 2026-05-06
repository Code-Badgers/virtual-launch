package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.service.ReviewAnalysisPromptGenerator;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.GeminiApiClient;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.dto.ReviewAnalysisAiResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final RawReviewRepository rawReviewRepository;
    private final CompetitorProductRepository competitorProductRepository;
    private final GeminiApiClient geminiApiClient;
    private final ReviewAnalysisPromptGenerator promptGenerator;

    @Retryable(
            retryFor = { WebClientResponseException.TooManyRequests.class },
            maxAttempts = 3, // 최대 3회 재시도
            backoff = @Backoff(delay = 1500) // 1.5초 후 재시도
    )
    public void analyzeAndSaveReviews(Long competitorProductId) {
        CompetitorProduct product = competitorProductRepository.findById(competitorProductId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<RawReview> rawReviews = rawReviewRepository.findByCompetitorProduct_CompetitorProductId(competitorProductId);
        // 리뷰 개수 확인
        long reviewCount = rawReviewRepository.countByCompetitorProduct_CompetitorProductId(competitorProductId);

        // 텍스트 가공
        String formattedReviews = rawReviews.stream()
                .map(review -> String.format("(별점: %d) %s", review.getStarRating(), review.getOriginalContent()))
                .collect(Collectors.joining("\n"));

        if (reviewCount == 0) { // 리뷰가 없는 경우 AI 분석 로직을 건너뛰고 로그만 남김
            log.warn("분석할 리뷰가 없습니다.\n 경쟁사 제품 ID: {}\n 경쟁사 제품 url: {}\n", competitorProductId, product.getCompetitorProductUrl());
            return;
        }

        // AI 프롬프트 생성 (크롤링한 리뷰가 존재할 경우)
        String prompt = promptGenerator.generate(formattedReviews);

        ReviewAnalysisAiResponse response = geminiApiClient.generateText(prompt, ReviewAnalysisAiResponse.class);

        log.info("AI 리뷰 분석 결과 - 부/긍정 점수: {}\n, 리뷰 태그: {}\n, 긍정 포인트: {}\n, 불편 포인트: {}\n",
                response.sentimentScore(), response.reviewTags(), response.positivePoints(), response.painPoints());

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
