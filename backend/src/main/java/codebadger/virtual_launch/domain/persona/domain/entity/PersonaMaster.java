package codebadger.virtual_launch.domain.persona.domain.entity;

import codebadger.virtual_launch.domain.persona.infrastructure.AgeRange;
import codebadger.virtual_launch.domain.persona.infrastructure.IndustryCode;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "persona_master")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PersonaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personaId;

    // 💡 String -> AgeRange Enum으로 변경
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AgeRange ageRange;

    @Column(nullable = false, length = 10)
    private String gender; // 성별

    // 💡 String occupation -> IndustryCode Enum으로 변경
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IndustryCode industryCode;

    @Column(nullable = false, length = 20)
    private String incomeLevel; // 산출된 소득 수준

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PurchaseCriteria purchaseCriteria; // 주요 구매 기준

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String systemPrompt; // 최종 조립된 프롬프트

    public static PersonaMaster create(AgeRange ageRange,
                                       String gender,
                                       IndustryCode industryCode,
                                       String incomeLevel,
                                       PurchaseCriteria criteria,
                                       String systemPrompt) {
        return PersonaMaster.builder()
                .ageRange(ageRange)
                .gender(gender)
                .industryCode(industryCode)
                .incomeLevel(incomeLevel)
                .purchaseCriteria(criteria)
                .systemPrompt(systemPrompt)
                .build();
    }
}