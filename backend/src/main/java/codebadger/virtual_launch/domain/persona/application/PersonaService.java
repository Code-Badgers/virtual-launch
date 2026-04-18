package codebadger.virtual_launch.domain.persona.application;

import codebadger.virtual_launch.domain.persona.domain.entity.PersonaMaster;
import codebadger.virtual_launch.domain.persona.domain.repository.PersonaMasterRepository;
import codebadger.virtual_launch.domain.persona.presentation.PersonaCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaMasterRepository personaMasterRepository;

    @Transactional
    public Long createPersona(PersonaCreateRequest dto) {
        // 1. 프롬프트 조립
        String finalPrompt = buildSystemPrompt(dto);

        // 2. 엔티티 생성 및 저장
        PersonaMaster persona = PersonaMaster.create(
                dto.ageGroup(),
                dto.gender(),
                dto.incomeLevel(),
                dto.purchaseCriteria(),
                finalPrompt
        );

        return personaMasterRepository.save(persona).getPersonaId();
    }

    private String buildSystemPrompt(PersonaCreateRequest dto) {
        StringBuilder prompt = new StringBuilder();

        // [기본 정체성]
        prompt.append(String.format("당신은 %s 연령대의 %s이며, 현재 %s(이)라는 직업을 가지고 있습니다. ",
                dto.ageGroup(), dto.gender(), dto.occupation()));

        prompt.append(String.format("소득 수준은 %s 정도입니다. ", dto.incomeLevel()));

        // [구매 성향]
        prompt.append(String.format("당신은 제품을 구매할 때 '%s'을(를) 가장 중요하게 생각합니다. ",
                dto.purchaseCriteria().getTitle()));
        prompt.append(dto.purchaseCriteria().getDescription()).append(". ");

        // [추가 성향 - 선택적 필드 처리]
        if (dto.personality() != null && !dto.personality().isBlank()) {
            prompt.append(String.format("성격은 %s한 편입니다. ", dto.personality()));
        }

        if (dto.lifestyle() != null && !dto.lifestyle().isBlank()) {
            prompt.append(String.format("평소 라이프스타일은 %s입니다. ", dto.lifestyle()));
        }

        // [최종 지시]
        prompt.append("이러한 배경 정보를 바탕으로, 제품에 대해 당신의 성향이 드러나는 구체적이고 솔직한 리뷰를 작성해 주세요.");

        return prompt.toString();
    }
}