package codebadger.virtual_launch.domain.crawling.domain.repository;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawReviewRepository extends JpaRepository<RawReview, Long> {
}
