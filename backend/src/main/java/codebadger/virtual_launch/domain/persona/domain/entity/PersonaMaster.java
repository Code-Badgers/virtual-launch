package codebadger.virtual_launch.domain.persona.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "persona_master")
@NoArgsConstructor
@Getter
@Entity
public class PersonaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaId;

    @Column(nullable = false, length = 20)
    private String ageGroup; // 연령대 (예: "20대", "30대")

    @Column(length = 10)
    private String gender; // 성별

    @Column(length = 20)
    private String incomeLevel; // 소득 수준

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PurchaseCriteria purchaseCriteria; // 주요 구매 기준

    @Lob // 대용량 텍스트 처리를 위한 설정
    @Column(nullable = false, columnDefinition = "TEXT")
    private String systemPrompt; // 직업, 성격 등이 합쳐진 최종 프롬프트

}
