package codebadger.virtual_launch.domain.crawling.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrawlingResultDto {
    private int totalReviewCount;
    private List<RawReview> reviews;
}
