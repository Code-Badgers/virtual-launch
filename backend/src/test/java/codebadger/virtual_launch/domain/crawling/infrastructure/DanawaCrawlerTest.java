package codebadger.virtual_launch.domain.crawling.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import codebadger.virtual_launch.domain.crawling.domain.RawReview;
import java.util.List;
import org.junit.jupiter.api.Test;

class DanawaCrawlerTest {

    @Test
    void crawlReviews() {
        DanawaCrawler crawler = new DanawaCrawler();
        List<RawReview> result = crawler.crawlReviews("위닉스 제습기");

        // 리뷰 첫 번째 페이지에서의 개수인 10을 반환 / 전체 리뷰 개수를 반환하려면 id=productOpinionTabCount 으로 수정 필요
        System.out.println("수집된 리뷰 개수: " + result.size());

        // 평균 평점 : id=star_fit 을 통해 계산하기

        // 첫 번째 리뷰의 내용과 별점을 출력해서 데이터가 정확한지 확인
        if (!result.isEmpty()) {
            System.out.println("첫 번째 리뷰 내용: " + result.get(0).getOriginalContent());
            // System.out.println("첫 번째 리뷰 별점: " + result.get(0).getStarRating());
        }
    }

    @Test
    void supports() {
        // supports 메서드가 잘 동작하는지 확인하는 테스트
        DanawaCrawler crawler = new DanawaCrawler();
        assert(crawler.supports("danawa") == true);
    }
}