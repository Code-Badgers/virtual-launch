package codebadger.virtual_launch.domain.simulation.domain.entity;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "product_spec")
public class ProductSpec extends BaseTimeEntity { // 가상 런칭 제품 스펙

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // 외래 키 - 여러 제품 스펙은 하나의 카테고리를 가질 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "선택한 카테고리")
    private Category category;

    // 외래 키 - 여러 제품 스펙은 한 명의 회원에 속한다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Schema(description = "제품 스펙을 등록한 회원")
    private Member member;

    @Column(nullable = false, length = 100)
    @Schema(description = "제품명")
    private String productName;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "제품 상세 설명")
    private String productDescription;

    @Schema(description = "제품 대표 이미지 URL")
    private String productImageUrl;

    @Schema(description = "출시 예정일 (ISO 8601 형식)", example = "2026-10-15T09:00:00+09:00")
    private OffsetDateTime plannedLaunchDate;

    @Schema(description = "희망 출시 가격")
    private BigDecimal plannedPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_specs", columnDefinition = "jsonb")
    private Map<String, Map<String, RequiredSpec>> detailedSpecs; // 제품 상세 스펙 (가변 데이터)
}