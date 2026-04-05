package codebadger.virtual_launch.domain.simulation.domain.entity;

import codebadger.virtual_launch.common.domain.BaseTimeEntity;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "simulation_project")
public class SimulationProject extends BaseTimeEntity { // 시뮬레이션 시작 시 사용

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long projectId;

    // 외래 키 - 여러 시뮬레이션 프로젝트는 하나의 제품 스펙을 가질 수 있다 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductSpec productSpec;

    // 외래 키 - 여러 시뮬레이션 프로젝트는 한 명의 회원을 가질 수 있다 (N:1)

    // 외래 키 - 여러 시뮬레이션 프로젝트는 하나의 제품 스펙을 가질 수 있다 (N:1)
    @OneToMany(mappedBy = "simulationProject", cascade = CascadeType.ALL)
    private List<MatchedCompetitor> matchedCompetitors = new ArrayList<>();

    @Schema(description = "프로젝트명")
    private String projectName;

    @Schema(description = "프로젝트 설명")
    private String projectDescription;

    @Schema(description = "시뮬레이션 진행 상태")
    private SimulationStatus simulationStatus;

    // 시뮬레이션 상태 업데이트 메서드
    public void updateStatus(SimulationStatus newStatus) {
        this.simulationStatus = newStatus;
    }
}
