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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCrawlingService {

    private final DanawaReviewCrawler danawaReviewCrawler;
    private final RawReviewRepository rawReviewRepository;
    private final CompetitorProductRepository competitorProductRepository;

    @Async
    @Transactional
    public void crawlReviews(String keyword, Long competitorProductId) {

        try{
            CompetitorProduct competitorProduct = competitorProductRepository.findById(competitorProductId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMPETITOR_PRODUCT_NOT_FOUND));

            // 크롤링 수행
            ReviewsCrawlingResultDto crawlingResult = danawaReviewCrawler.crawlReviews(keyword);
            List<RawReview> reviews = crawlingResult.getReviews();

            if(reviews.isEmpty() || reviews == null){
                log.warn("크롤링된 리뷰가 없습니다. 키워드: {}", keyword);
                return;
            }

            // 수집된 리뷰에 카테고리 연결하기

            // 크롤링 결과 저장
            rawReviewRepository.saveAll(reviews);

        } catch (Exception e) {
            log.error("리뷰 크롤링 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("리뷰 크롤링 비동기 트랜잭션 롤백 처리", e);
        }
    }

}
