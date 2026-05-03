package codebadger.virtual_launch.domain.persona.application;

import codebadger.virtual_launch.domain.persona.domain.entity.PersonaMaster;
import codebadger.virtual_launch.domain.persona.domain.entity.PurchaseCriteria;
import codebadger.virtual_launch.domain.persona.domain.repository.PersonaMasterRepository;
import codebadger.virtual_launch.domain.persona.infrastructure.AgeRange;
import codebadger.virtual_launch.domain.persona.infrastructure.IndustryCode;
import codebadger.virtual_launch.domain.persona.infrastructure.KosisApiClient;
import codebadger.virtual_launch.domain.persona.presentation.PersonaCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaMasterRepository personaMasterRepository;

    @Mock
    private KosisApiClient kosisApiClient; // 새로운 의존성 모킹

    @InjectMocks
    private PersonaService personaService;

    @Test
    @DisplayName("KOSIS 데이터를 기반으로 급여를 산출하여 페르소나가 성공적으로 저장된다.")
    void shouldPersonaCreateSuccessfully() {
        // [Given]
        PersonaCreateRequest request = new PersonaCreateRequest(
                AgeRange.UNDER_29,              // Enum 타입 적용
                "남성",
                IndustryCode.INFO_COMMUNICATION, // Enum 타입 적용
                "4,000만원 이상",
                PurchaseCriteria.COST_EFFECTIVE,
                "분석적이고 신중한 성격",
                "자기계발을 즐기는 라이프스타일"
        );

        // KOSIS API 응답 모킹 (테스트의 일관성을 위해 고정값 반환)
        given(kosisApiClient.getMonthlySalaryByAge(any())).willReturn(Mono.just(3000000L));
        given(kosisApiClient.getMonthlySalaryByIndustry(any())).willReturn(Mono.just(5000000L));

        // 가중치 산출 예상 소득: (300만 * 0.7) + (500만 * 0.3) = 360만원
        String expectedIncome = "3600000";

        PersonaMaster savedPersona = PersonaMaster.create(
                request.ageRange(),
                request.gender(),
                request.industryCode(),
                expectedIncome,
                request.purchaseCriteria(),
                "조립된 시스템 프롬프트"
        );

        Long expectedId = 1L;
        ReflectionTestUtils.setField(savedPersona, "personaId", expectedId);
        given(personaMasterRepository.save(any(PersonaMaster.class))).willReturn(savedPersona);

        // [When & Then] StepVerifier를 사용한 리액티브 스트림 검증
        StepVerifier.create(personaService.createPersona(request))
                .expectNext(expectedId) // 예상되는 저장된 ID 확인
                .verifyComplete();     // 스트림이 정상 종료되는지 확인

        // 레포지토리 저장 호출 여부 검증
        verify(personaMasterRepository, atLeastOnce()).save(any(PersonaMaster.class));
    }
}