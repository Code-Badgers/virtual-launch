package codebadger.virtual_launch.domain.persona.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KosisApiClientTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            // 미정의 필드 무시 설정을 빈 레벨에서 해주면 DTO의 @JsonIgnoreProperties가 없어도 안전합니다.
            return new ObjectMapper()
                    .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    @Autowired
    private KosisApiClient kosisApiClient;

    @Test
    @DisplayName("29세 이하의 월평균 급여 데이터를 KOSIS에서 정상적으로 가져온다")
    void testGetMonthlySalaryByAge() {
        // given
        AgeRange ageRange = AgeRange.UNDER_29;

        // when
        Mono<Long> monthlySalaryByAge = kosisApiClient.getMonthlySalaryByAge(ageRange);

        // then
        Long salary = monthlySalaryByAge.block();
        assertNotNull(salary);
        assertTrue(salary > 0);
        System.out.println("✅ [연령별] 최종 결과 금액: " + salary + "원");
    }

    @Test
    @DisplayName("정보통신업의 월평균 급여 데이터를 KOSIS에서 정상적으로 가져온다")
    void testGetMonthlySalaryByIndustry() {
        // given
        // 개발자 직군이 포함된 '정보통신업'으로 테스트해봅니다.
        IndustryCode industryCode = IndustryCode.INFO_COMMUNICATION;

        // when
        Mono<Long> monthlySalaryByIndustry = kosisApiClient.getMonthlySalaryByIndustry(industryCode);

        // then
        Long salary = monthlySalaryByIndustry.block();

        System.out.println("========================================");
        System.out.println("✅ [" + industryCode.getDescription() + "] 최종 결과 금액: " + salary + "원");
        System.out.println("========================================");

        assertNotNull(salary);
        assertTrue(salary > 0, "조회된 급여가 0보다 커야 합니다.");
    }
}