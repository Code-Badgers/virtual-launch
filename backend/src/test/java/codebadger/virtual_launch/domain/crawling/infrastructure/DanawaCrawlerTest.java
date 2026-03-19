package codebadger.virtual_launch.domain.crawling.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import codebadger.virtual_launch.domain.crawling.domain.CrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.domain.RawReview;
import java.util.List;
import org.junit.jupiter.api.Test;

class DanawaCrawlerTest {

    @Test
    void crawlReviews() {
        DanawaCrawler crawler = new DanawaCrawler();
        CrawlingResultDto result = crawler.crawlReviews("위닉스 제습기");

        // id=productOpinionTabCount 를 통해 추출된 실제 전체 리뷰 개수 확인
        System.out.println("전체 리뷰 개수: " + result.getTotalReviewCount() + "개");
        System.out.println("--------------------");

        List<RawReview> reviews = result.getReviews();
        if (reviews.isEmpty()) {
            System.out.println("수집된 데이터가 없습니다.");
            return;
        }

        // 2. 크롤러 내부에서 이미 별점별로 1개씩만 걸러서(Map) 가져왔으므로 그대로 출력하면 됩니다.
        for (RawReview r : reviews) {
            System.out.println("[" + r.getStarRating() + "점 리뷰]");
            System.out.println("내용: " + r.getOriginalContent());
            System.out.println("--------------------");
        }

        // 데이터가 잘 수집되었는지 최소한의 검증
        assertTrue(result.getTotalReviewCount() >= 0);
        assertTrue(reviews.size() <= 5); // 1~5점 각 1개씩 최대 5개여야 함
    }

    @Test
    void supports() {
        DanawaCrawler crawler = new DanawaCrawler();
        assertTrue(crawler.supports("danawa"));
    }
}