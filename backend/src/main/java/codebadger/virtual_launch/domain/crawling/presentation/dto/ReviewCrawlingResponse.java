package codebadger.virtual_launch.domain.crawling.presentation.dto;

import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import java.util.List;

public record ReviewCrawlingResponse(
        List<ReviewDetail> reviews,
        String processing) {
        public record ReviewDetail(
                String platform,
                String content,
                int starRating
        ) {
            public static ReviewDetail from(RawReview review) {
                return new ReviewDetail(
                        review.getPlatform(),
                        review.getOriginalContent(),
                        review.getStarRating()
                );
            }
        }
}
