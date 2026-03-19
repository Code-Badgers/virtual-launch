package codebadger.virtual_launch.domain.crawling.domain;

import java.util.List;

public interface ReviewCrawler {
    // 검색어를 주면 타겟 플랫폼에서 리뷰를 긁어와서 RawReview 리스트로 반환
    CrawlingResultDto crawlReviews(String keyword);

    // 해당 크롤러가 특정 플랫폼을 지원하는지 여부를 반환
    boolean supports(String platform);
}
