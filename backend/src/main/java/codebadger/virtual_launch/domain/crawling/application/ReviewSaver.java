package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReviewSaver {
    private final RawReviewRepository rawReviewRepository;

    @Transactional
    public void saveReviews(List<RawReview> reviews,  CompetitorProduct competitorProduct) {
        // 연관관계 매핑
        for(RawReview rawReview : reviews) {
            rawReview.setCompetitorProduct(competitorProduct);
        }

        // 크롤링 결과 저장
        rawReviewRepository.saveAll(reviews);
    }

}
