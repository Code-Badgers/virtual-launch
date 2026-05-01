package codebadger.virtual_launch.domain.crawling.domain.repository;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawReviewRepository extends JpaRepository<RawReview, Long> {
    // 경쟁사 제품에 대한 모든 리뷰 조회
    List<RawReview> findByCompetitorProduct_CompetitorProductId(Long competitorProductId);

    // 경쟁사 제품에 대한 리뷰 개수 확인
    long countByCompetitorProduct_CompetitorProductId(Long competitorProductId);
}
