package codebadger.virtual_launch.domain.crawling.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingRequest;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/competitors/crawl")
@RequiredArgsConstructor
public class CrawlingController {

    private final ReviewCrawlingService reviewCrawlingService;

    // 특정 키워드로 크롤링을 명령하고 db저장
    @PostMapping("/reviews")
    @Operation(summary = "제품명에 따른 리뷰 크롤링", description = "제품명에 따른 리뷰 크롤링을 수행하고, 크롤링된 리뷰 데이터를 DB에 저장합니다")
    public SuccessResponse<ReviewCrawlingResponse> startCrawling(@RequestBody ReviewCrawlingRequest request) {

        // 비동기 처리로 인해 바로 결과를 반환할 수 없으므로, 크롤링이 완료된 후 DB에서 데이터를 조회하여 반환하는 방식으로 추후 변경 예정
        // CrawlingResultDto resultDto = reviewCrawlingService.crawlReviews(request.keyword());

        // 크롤링 작업만 비동기로 트리거 (반환값을 기다리지 않음)
        reviewCrawlingService.crawlReviews(request.keyword());

//        List<ReviewDetail> details = resultDto.getReviews().stream()
//                .map(ReviewCrawlingResponse.ReviewDetail::from)
//                .collect(Collectors.toList());

        ReviewCrawlingResponse response = new ReviewCrawlingResponse(
                null,
                "PROCESSING"
        );

        return SuccessResponse.ok(response, "리뷰 크롤링이 시작되었습니다.\n 완료 후 결과를 확인해주세요.");
    }
}
