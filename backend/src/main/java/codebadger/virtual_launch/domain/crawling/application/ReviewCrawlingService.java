package codebadger.virtual_launch.domain.crawling.application;

import codebadger.virtual_launch.domain.crawling.domain.CrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import codebadger.virtual_launch.domain.crawling.domain.repository.RawReviewRepository;
import codebadger.virtual_launch.domain.crawling.infrastructure.DanawaBaseCrawler;
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

    private final DanawaBaseCrawler danawaBaseCrawler;
    private final RawReviewRepository rawReviewRepository;

    @Async
    @Transactional
    public void crawlReviews(String keyword) {
        // 크롤링 수행
        CrawlingResultDto crawlingResult = danawaBaseCrawler.crawlReviews(keyword);
        List<RawReview> reviews = crawlingResult.getReviews();

        // 수집된 리뷰에 카테고리 연결하기

        // 크롤링 결과 저장
//        rawReviewRepository.saveAll(reviews);
//        return crawlingResult;
    }
}
