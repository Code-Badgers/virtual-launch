package codebadger.virtual_launch.domain.crawling.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.crawling.domain.CrawlingResultDto;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingRequest;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse.ReviewDetail;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "제품명에 따른 리뷰 크롤링", description = "제품명에 따른 리뷰 크롤링을 수행하고, 크롤링된 리뷰 데이터를 DB에 저장합니다")
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
