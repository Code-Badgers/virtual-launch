package codebadger.virtual_launch.domain.crawling.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.crawling.application.CompetitorCrawlingService;
import codebadger.virtual_launch.domain.crawling.application.ReviewCrawlingService;
import codebadger.virtual_launch.domain.crawling.presentation.dto.CompetitorCrawlingRequest;
import codebadger.virtual_launch.domain.crawling.presentation.dto.CompetitorCrawlingResponse;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingRequest;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse;
import codebadger.virtual_launch.domain.crawling.presentation.dto.ReviewCrawlingResponse.ReviewDetail;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/competitors/crawl")
@RequiredArgsConstructor
public class CrawlingController {

    private final ReviewCrawlingService reviewCrawlingService;
    private final CompetitorCrawlingService competitorCrawlingService;

    // 특정 키워드로 리뷰 크롤링을 명령하고 db저장
    @PostMapping("/reviews")
    @Operation(summary = "제품명에 따른 리뷰 크롤링", description = "제품명에 따른 리뷰 크롤링을 수행하고, 크롤링된 리뷰 데이터를 DB에 저장합니다")
    public SuccessResponse<ReviewCrawlingResponse> startReviewCrawling(@RequestBody ReviewCrawlingRequest request) {

        // 크롤링 작업만 비동기로 트리거 (반환값을 기다리지 않음)
        reviewCrawlingService.crawlReviews(request.keyword(), request.competitorProductId());

        ReviewCrawlingResponse response = new ReviewCrawlingResponse(
                null,
                "PROCESSING"
        );

        return SuccessResponse.ok(response, "리뷰 크롤링이 시작되었습니다.\n 완료 후 결과를 확인해주세요.");
    }

    // 특정 키워드로 크롤링된 리뷰 데이터를 DB에서 조회하여 반환
    @GetMapping("/reviews")
    @Operation(summary = "제품명에 따른 리뷰 크롤링 결과 조회")
    public SuccessResponse<ReviewCrawlingResponse> getReviewCrawlingResult(@RequestParam("competitorProductId") Long competitorProductId) {

        return null;
    }

    @PostMapping("/competitor-specs")
    @Operation(summary = "경쟁사 상세 스펙 크롤링")
    public SuccessResponse<CompetitorCrawlingResponse> startCompetitorSpecCrawling(@Valid @RequestBody CompetitorCrawlingRequest request) {
        competitorCrawlingService.crawlSpecs(request.keyword(), request.productId());

        return SuccessResponse.ok(null, "경쟁사 제품 스펙 크롤링이 시작되었습니다.\n 완료 후 결과를 확인해주세요.");
    }
}
