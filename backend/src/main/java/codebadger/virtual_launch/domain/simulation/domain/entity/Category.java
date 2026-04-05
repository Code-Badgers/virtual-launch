package codebadger.virtual_launch.domain.simulation.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false)
    @Schema(description = "카테고리 대분류명", example = "노트북")
    private String categoryName; // 카테고리 대분류명

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_specs", columnDefinition = "jsonb")
    private Map<String, Map<String, RequiredSpec>> requiredSpecs; // 필수 입력 스펙 폼 데이터

    // 하나의 카테고리는 여러 제품 스펙을 가질 수 있다 (1:N)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductSpec> productSpecs = new ArrayList<>();

    // 하나의 카테고리는 여러 경쟁사 제품 스펙을 가질 수 있다 (1:N)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<CompetitorProduct> competitorProducts = new ArrayList<>();
}
