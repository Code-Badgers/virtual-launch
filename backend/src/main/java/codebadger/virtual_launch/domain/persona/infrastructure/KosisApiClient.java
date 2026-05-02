package codebadger.virtual_launch.domain.persona.infrastructure;

import codebadger.virtual_launch.domain.persona.presentation.KosisIncomeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Component
public class KosisApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public KosisApiClient(WebClient webClient,
                          ObjectMapper objectMapper,
                          @Value("${kosis.api.url}") String baseUrl,
                          @Value("${kosis.api.key}") String apiKey) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.webClient = webClient.mutate()
                .uriBuilderFactory(factory)
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    /**
     * 산업별 월평균 급여 조회 (DT_118N_LCE305)
     */
    public Mono<Long> getMonthlySalaryByIndustry(IndustryCode industryCode) {
        return fetchSalaryData(
                "DT_118N_LCE305",
                "13103732814DD_5",
                "13102732818TYPES.00",
                industryCode.getCode()
        );
    }

    /**
     * 나이대별 월평균 급여 조회 (DT_118N_LCE0004)
     */
    public Mono<Long> getMonthlySalaryByAge(AgeRange ageRange) {
        return fetchSalaryData(
                "DT_118N_LCE0004",
                "13103732814DD_5",
                "13102732817TYPES.00",
                ageRange.getCode()
        );
    }

    /**
     * [공통 로직]
     * 1. 전달받은 파라미터를 그대로 사용
     * 2. 응답이 배열([])이든 객체({})든 유연하게 처리
     */
    private Mono<Long> fetchSalaryData(String tblId, String itmId, String objL1, String categoryCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "getList")
                        .queryParam("apiKey", apiKey)
                        .queryParam("orgId", "118")
                        .queryParam("tblId", tblId)
                        .queryParam("itmId", itmId)
                        .queryParam("objL1", objL1)
                        .queryParam("objL2", categoryCode)
                        .queryParam("objL3", "").queryParam("objL4", "")
                        .queryParam("objL5", "").queryParam("objL6", "")
                        .queryParam("objL7", "").queryParam("objL8", "")
                        .queryParam("format", "json")
                        .queryParam("jsonVD", "Y")
                        .queryParam("prdSe", "Y")
                        .queryParam("newEstPrdCnt", "1")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(json);

                        // KOSIS 에러 응답 확인
                        if (node.has("err")) {
                            System.err.println("❌ KOSIS API 에러 응답 [" + tblId + "]: " + json);
                            return Mono.empty();
                        }

                        // 응답 구조 유연화 (배열 vs 객체)
                        com.fasterxml.jackson.databind.JsonNode targetNode = node.isArray() ? node.get(0) : node;
                        if (targetNode == null || targetNode.isMissingNode()) return Mono.empty();

                        KosisIncomeResponse response = objectMapper.treeToValue(targetNode, KosisIncomeResponse.class);
                        return Mono.just(response);
                    } catch (Exception e) {
                        System.err.println("❌ 파싱 실패: " + e.getMessage() + " | 원본: " + json);
                        return Mono.error(e);
                    }
                })
                .map(KosisIncomeResponse::getConvertedAmount)
                .defaultIfEmpty(0L)
                .onErrorReturn(0L);
    }
}