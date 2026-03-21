package codebadger.virtual_launch.domain.crawling.domain;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrawlingResultDto {
    private int totalReviewCount;
    private List<RawReview> reviews;
}
