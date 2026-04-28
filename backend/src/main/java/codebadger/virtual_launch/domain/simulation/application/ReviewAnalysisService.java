package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.simulation.domain.service.ReviewAnalysisPromptGenerator;
import codebadger.virtual_launch.domain.simulation.infrastructure.ai.GeminiApiClient;
import java.util.List;
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

    }

}
