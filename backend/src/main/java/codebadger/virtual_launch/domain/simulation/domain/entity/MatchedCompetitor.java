package codebadger.virtual_launch.domain.simulation.domain.entity;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "matched_competitor")
public class MatchedCompetitor extends BaseTimeEntity { // 중간 매핑 테이블 (런칭 예정 제품, 경쟁사 제품)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchedId;

    // 외래 키 - 여러 개의 시뮬레이션 프로젝트는 하나의 중간 매핑 테이블과 매칭될 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private SimulationProject simulationProject;

    // 외래 키 - 여러 개의 경쟁사 제품은 하나의 중간 매핑 테이블과 매칭될 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_product_id", nullable = false)
    private CompetitorProduct competitorProduct;

    private Double similarityScore; // 유사도 점수

    private Integer matchRank; // 유사도 순위

    @Column(columnDefinition = "TEXT")
    private String feedback; // AI 종합 분ㅅ거 (점수 산정 이유) - AiMatchResult

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Double> itemScores; // 항목별 세부 점수 - AiMatchResult

    @Builder
    public MatchedCompetitor(SimulationProject simulationProject,
            CompetitorProduct competitorProduct,
            Double similarityScore,
            Integer matchRank,
            String feedback,
            Map<String, Double> itemScores) {
        this.simulationProject = simulationProject;
        this.competitorProduct = competitorProduct;
        this.similarityScore = similarityScore;
        this.matchRank = matchRank;
        this.feedback = feedback;
        this.itemScores = itemScores;
    }
}