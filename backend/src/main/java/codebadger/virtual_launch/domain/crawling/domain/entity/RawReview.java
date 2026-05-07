package codebadger.virtual_launch.domain.crawling.domain.entity;

import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "raw_review")
public class RawReview { // 실제 리뷰 - 개별

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rawReviewId;

    // 외래 키 - 여러 개의 리뷰는 하나의 경쟁사 제품을 가질 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_product_id")
    @Schema(description = "해당 리뷰가 속한 경쟁사 제품")
    private CompetitorProduct competitorProduct;

    private String platform; // 리뷰 출처 (네이버, 다나와 등)

    @Column(columnDefinition = "TEXT")
    private String originalContent; // 원본 리뷰 내용
    private int starRating; // 별점 (1~5)
}