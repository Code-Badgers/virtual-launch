package codebadger.virtual_launch.domain.persona.infrastructure;

import codebadger.virtual_launch.domain.persona.presentation.KosisIncomeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class KosisApiClient {

    private final WebClient webClient;
    private final String apiKey;

    // 고용노동부 및 연령별 임금 통계표 관련 상수
    private static final String ORG_ID = "118";
    private static final String AGE_SALARY_TBL_ID = "DT_118N_LCE0004";
    private static final String ITEM_ID_MONTHLY_SALARY = "13103732814DD_5"; // 월급여액
    private static final String CATEGORY_ALL_WORKERS = "13102732817TYPES.00"; // 전체근로자

    public KosisApiClient(WebClient.Builder webClientBuilder,
                          @Value("${kosis.api.url}") String baseUrl,
                          @Value("${kosis.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    /**
     * 특정 연령대의 최신 월평균 급여 정보를 가져옵니다.
     * @param ageRange 연령대 Enum
     * @return 환산된 급여 금액 (Long)
     */
    public Mono<Long> getMonthlySalaryByAge(AgeRange ageRange) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/statisticsData.do")
                        .queryParam("method", "getList")
                        .queryParam("apiKey", apiKey)
                        .queryParam("orgId", ORG_ID)
                        .queryParam("tblId", AGE_SALARY_TBL_ID)
                        .queryParam("itmId", ITEM_ID_MONTHLY_SALARY)
                        .queryParam("objL1", CATEGORY_ALL_WORKERS) // 고용형태: 전체근로자
                        .queryParam("objL2", ageRange.getCode())   // 연령: 선택한 연령대 코드
                        .queryParam("format", "json")
                        .queryParam("jsonVD", "Y")
                        .queryParam("newEstPrdCnt", "1") // 가장 최신 연도 데이터 1건만 요청
                        .build())
                .retrieve()
                .bodyToFlux(KosisIncomeResponse.class)
                .next() // Flux의 첫 번째 요소(최신 데이터)를 Mono로 전환
                .map(KosisIncomeResponse::getConvertedAmount) // "천원" 단위를 "원"으로 환산
                .defaultIfEmpty(0L) // 데이터가 없을 경우 기본값 0 반환
                .onErrorReturn(0L); // 에러 발생 시 시스템 중단 방지를 위해 0 반환
    }
}
