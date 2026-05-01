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

    // 1. 테스트 환경에서 사용할 ObjectMapper를 직접 정의합니다.
    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
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

        System.out.println("========================================");
        System.out.println("✅ 최종 결과 금액: " + salary + "원");
        System.out.println("========================================");

        assertNotNull(salary);
        assertTrue(salary > 0, "조회된 급여가 0보다 커야 합니다.");
    }
}