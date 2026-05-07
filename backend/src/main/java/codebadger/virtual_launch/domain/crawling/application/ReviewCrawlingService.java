package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.crawling.domain.ReviewsCrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.crawling.infrastructure.DanawaReviewCrawler;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCrawlingService {

    private final DanawaReviewCrawler danawaReviewCrawler;
    private final RawReviewRepository rawReviewRepository;
    private final CompetitorProductRepository competitorProductRepository;
    private final ReviewSaver reviewSaver;

    @Async
    public CompletableFuture<Void> crawlReviews(String keyword, Long competitorProductId, Integer limit) {
        // 크롤링할 리뷰 갯수 미입력 시 기본값 5
        int targetLimit = (limit != null) ? limit : 5;

        try{
            CompetitorProduct competitorProduct = competitorProductRepository.findById(competitorProductId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMPETITOR_PRODUCT_NOT_FOUND));

            // 크롤링 수행
            ReviewsCrawlingResultDto crawlingResult = danawaReviewCrawler.crawlReviews(keyword, targetLimit);
            List<RawReview> reviews = crawlingResult.getReviews();

            if(reviews == null || reviews.isEmpty()){
                log.warn("크롤링된 리뷰가 없습니다. 키워드: {}", keyword);
                return CompletableFuture.completedFuture(null);
            }

            log.info("리뷰 크롤링이 완료되었습니다. 크롤링된 리뷰 원본: {}", reviews.stream()
                    .map(RawReview::getOriginalContent)
                    .toList());

            reviewSaver.saveReviews(reviews, competitorProduct);

        } catch (Exception e) {
            log.error("리뷰 크롤링 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("리뷰 크롤링 비동기 트랜잭션 롤백 처리", e);
        }
        // 리뷰 크롤링이 완료되었음을 알림
        return CompletableFuture.completedFuture(null);
    }
}
