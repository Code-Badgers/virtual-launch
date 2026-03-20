package codebadger.virtual_launch.domain.crawling.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.crawling.domain.CrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingRequest;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse.ReviewDetail;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/competitors/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCrawlingService reviewCrawlingService;

    // 특정 키워드로 크롤링을 명령하고 db저장
    @PostMapping("/crawl")
    public SuccessResponse<ReviewCrawlingResponse> startCrawling(@RequestBody ReviewCrawlingRequest request) {

        CrawlingResultDto resultDto = reviewCrawlingService.crawlAndSaveReviews(request.keyword());

        List<ReviewDetail> details = resultDto.getReviews().stream()
                .map(ReviewCrawlingResponse.ReviewDetail::from)
                .collect(Collectors.toList());

        ReviewCrawlingResponse response = new ReviewCrawlingResponse(
                details
        );

        return SuccessResponse.ok(response, "경쟁사 리뷰 크롤링 및 저장이 완료되었습니다");
    }
}
