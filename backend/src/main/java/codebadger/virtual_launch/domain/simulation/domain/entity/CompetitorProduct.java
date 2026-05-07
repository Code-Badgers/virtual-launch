package codebadger.virtual_launch.domain.simulation.domain.entity;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
import codebadger.virtual_launch.domain.crawling.domain.entity.RawReview;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "competitor_product")
public class CompetitorProduct extends BaseTimeEntity { // 경쟁사 제품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long competitorProductId;

    // 외래 키 - 여러 경쟁사 제품 스펙은 하나의 카테고리를 가질 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "선택한 카테고리")
    private Category category;

    // 외래 키 - 한 명의 경쟁사 제품은 여러 개의 리뷰를 가질 수 있다 (1:N)
    @Builder.Default
    @OneToMany(mappedBy = "competitorProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RawReview> rawReviews = new ArrayList<>();

    @Schema(description = "브랜드명")
    private String brandName;

    @Schema(description = "제품명")
    private String modelName;

    @Schema(description = "출시 가격")
    private BigDecimal releasePrice;

    @Schema(description = "현재 가격")
    private BigDecimal currentPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_specs", columnDefinition = "jsonb")
    private Map<String, Map<String, RequiredSpec>> detailedSpecs; // 제품 상세 스펙 (가변 데이터)

    @Schema(description = "경쟁사 제품 대표 이미지 URL")
    private String competitorProductImageUrl;

    @Schema(description = "제품 출시일 (ISO 8601 형식)", example = "2026-10-15T09:00:00+09:00")
    private OffsetDateTime releaseDate;

    @Schema(description = "평균 별점")
    private double averageStarRating;

    @Schema(description = "전체 리뷰 수")
    private int totalReviewCount;

    @Schema(description = "경쟁사 제품 상세 페이지 URL")
    private String competitorProductUrl;

    @Schema(description = "마지막 크롤링 시점")
    private OffsetDateTime lastCrawledAt;

    @Column
    @Range(min = 0, max = 10)
    private double sentiment; // 긍정, 부정 스코어 점수 (0.0 ~ 10.0)

    @Column(columnDefinition = "TEXT")
    private String reviewTags; // 리뷰에서 추출된 키워드 (콤마로 구분)

    @Column(columnDefinition = "TEXT")
    private String positivePoints; // 핵심 만족 사항

    @Column(columnDefinition = "TEXT")
    private String painPoints; // 핵심 불만 사항

    public void updateAnalysisResult(double sentiment, String reviewTags, String positivePoints, String painPoints) {
        this.sentiment = sentiment;
        this.reviewTags = reviewTags;
        this.positivePoints = positivePoints;
        this.painPoints = painPoints;

    }
}
