package codebadger.virtual_launch.domain.crawling.domain;

public class RawReview { // 실제 리뷰 - 개별
    private Long rawReviewId;
    private String platform; // 리뷰 출처 (네이버, 다나와 등)
    private String originalContent; // 원본 리뷰 내용
    private int starRating; // 별점 (1~5)

    private Sentiment sentiment; // 긍정, 부정 스코어 점수
    private String keywords; // 리뷰에서 추출된 키워드 (콤마로 구분)
    private String painPoints; // 핵심 불만 사항
}
