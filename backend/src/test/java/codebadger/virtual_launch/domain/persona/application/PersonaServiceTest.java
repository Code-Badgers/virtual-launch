package codebadger.virtual_launch.domain.persona.application;

import codebadger.virtual_launch.domain.persona.domain.entity.PersonaMaster;
import codebadger.virtual_launch.domain.persona.domain.entity.PurchaseCriteria;
import codebadger.virtual_launch.domain.persona.domain.repository.PersonaMasterRepository;
import codebadger.virtual_launch.domain.persona.presentation.PersonaCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaMasterRepository personaMasterRepository;

    @InjectMocks
    private PersonaService personaService;

    @Test
    @DisplayName("사용자 입력 데이터를 바탕으로 페르소나가 성공적으로 생성되고 저장된다.")
    void shouldPersonaCreateSuccessfully() {
        // [Given] 테스트 환경 준비
        PersonaCreateRequest request = new PersonaCreateRequest(
                "20대",
                "남성",
                "소프트웨어 엔지니어",
                "4,000만원 이상",
                PurchaseCriteria.COST_EFFECTIVE, // 미리 정의된 Enum 값
                "분석적이고 신중한 성격",
                "자기계발을 즐기는 라이프스타일"
        );

        // 가짜 엔티티 생성 (DB 저장 후 ID가 할당된 상황을 모사)
        PersonaMaster savedPersona = PersonaMaster.create(
                request.ageGroup(),
                request.gender(),
                request.occupation(),
                request.incomeLevel(),
                request.purchaseCriteria(),
                "조립된 시스템 프롬프트"
        );

        // 엔티티의 ID는 보통 DB에서 생성되므로, Reflection을 이용해 강제로 ID를 주입합니다.
        Long expectedId = 1L;
        ReflectionTestUtils.setField(savedPersona, "personaId", expectedId);

        // 레포지토리의 save 메서드가 호출되면 위에서 만든 savedPersona를 반환하도록 설정
        given(personaMasterRepository.save(any(PersonaMaster.class))).willReturn(savedPersona);

        // [When] 실제 서비스 로직 실행
        Long resultId = personaService.createPersona(request);

        // [Then] 결과 검증
        assertThat(resultId).isNotNull();
        assertThat(resultId).isEqualTo(expectedId);

        // 실제로 save 메서드가 한 번 이상 호출되었는지 확인
        verify(personaMasterRepository, atLeastOnce()).save(any(PersonaMaster.class));
    }
}