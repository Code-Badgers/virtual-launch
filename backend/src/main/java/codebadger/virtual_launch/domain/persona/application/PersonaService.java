package codebadger.virtual_launch.domain.persona.application;

import codebadger.virtual_launch.domain.persona.domain.entity.PersonaMaster;
import codebadger.virtual_launch.domain.persona.domain.repository.PersonaMasterRepository;
import codebadger.virtual_launch.domain.persona.infrastructure.AgeRange;
import codebadger.virtual_launch.domain.persona.infrastructure.IndustryCode;
import codebadger.virtual_launch.domain.persona.infrastructure.KosisApiClient;
import codebadger.virtual_launch.domain.persona.presentation.PersonaCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaMasterRepository personaMasterRepository;
    private final KosisApiClient kosisApiClient;

    @Transactional
    public Mono<Long> createPersona(PersonaCreateRequest dto) {
        // 1. DTO에서 넘겨받은 Enum을 그대로 사용하여 통계 소득 산출
        return calculateStatisticIncome(dto.ageRange(), dto.industryCode())
                .flatMap(calculatedIncome -> {
                    // 2. 시스템 프롬프트 조립 (Enum의 한글 명칭 사용)
                    String finalPrompt = buildSystemPrompt(dto, calculatedIncome);

                    // 3. 엔티티 생성 및 저장
                    // PersonaMaster.create 메서드 파라미터 타입이 Enum으로 변경되었는지 확인 필요합니다.
                    PersonaMaster persona = PersonaMaster.create(
                            dto.ageRange(),       // AgeRange Enum
                            dto.gender(),
                            dto.industryCode(),   // IndustryCode Enum
                            String.valueOf(calculatedIncome),
                            dto.purchaseCriteria(),
                            finalPrompt
                    );

                    PersonaMaster saved = personaMasterRepository.save(persona);
                    log.info("✅ 페르소나 생성 완료: ID={}, 산출 소득={}원", saved.getPersonaId(), calculatedIncome);
                    return Mono.just(saved.getPersonaId());
                });
    }

    private Mono<Long> calculateStatisticIncome(AgeRange age, IndustryCode industry) {
        return Mono.zip(
                kosisApiClient.getMonthlySalaryByAge(age),
                kosisApiClient.getMonthlySalaryByIndustry(industry)
        ).map(tuple -> {
            long ageSalary = tuple.getT1();
            long industrySalary = tuple.getT2();

            // 가중치 적용 (나이대 70%, 산업군 30%)
            double finalSalary = (ageSalary * 0.7) + (industrySalary * 0.3);
            return Math.round(finalSalary);
        });
    }

    private String buildSystemPrompt(PersonaCreateRequest dto, long income) {
        StringBuilder prompt = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("###,###");

        // [기본 정체성]
        // 💡 중요: dto.ageRange().getDescription() 처럼 Enum에 정의된 한글 명칭을 사용하세요.
        // AgeRange Enum에도 IndustryCode처럼 getDescription() 메서드가 있다고 가정합니다.
        prompt.append(String.format("당신은 %s 연령대의 %s이며, 현재 %s 분야에서 종사하고 있습니다. ",
                dto.ageRange().getDescription(),
                dto.gender(),
                dto.industryCode().getDescription()));

        prompt.append(String.format("당신의 월평균 소득은 약 %s원입니다. ", formatter.format(income)));

        // [구매 성향]
        prompt.append(String.format("당신은 제품을 구매할 때 '%s'을(를) 가장 중요하게 생각합니다. ",
                dto.purchaseCriteria().getTitle()));
        prompt.append(dto.purchaseCriteria().getDescription()).append(". ");

        // [선택적 필드 처리]
        if (dto.personality() != null && !dto.personality().isBlank()) {
            prompt.append(String.format("성격은 %s한 편입니다. ", dto.personality()));
        }

        // 기존 DTO에 있던 lifestyle 필드도 프롬프트에 추가하면 더 풍성해집니다.
        if (dto.lifestyle() != null && !dto.lifestyle().isBlank()) {
            prompt.append(String.format("라이프스타일은 %s입니다. ", dto.lifestyle()));
        }

        prompt.append("이러한 경제적 배경과 성향을 바탕으로, 제품에 대해 당신의 정체성이 드러나는 구체적인 리뷰를 작성해 주세요.");

        return prompt.toString();
    }
}