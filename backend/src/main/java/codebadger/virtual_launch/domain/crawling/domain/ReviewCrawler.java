package codebadger.virtual_launch.domain.crawling.domain;

public interface ReviewCrawler {
    // 검색어를 주면 타겟 플랫폼에서 리뷰를 긁어와서 RawReview 리스트로 반환
    ReviewsCrawlingResultDto crawlReviews(String keyword);
}
