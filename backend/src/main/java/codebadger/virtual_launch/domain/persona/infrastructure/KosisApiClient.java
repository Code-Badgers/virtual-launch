package codebadger.virtual_launch.domain.persona.infrastructure;

import codebadger.virtual_launch.domain.persona.presentation.KosisIncomeResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class KosisApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper; // JSON 변환 전문가 📦

    public KosisApiClient(WebClient webClient,
                          ObjectMapper objectMapper,
                          @Value("${kosis.api.url}") String baseUrl,
                          @Value("${kosis.api.key}") String apiKey) {

        // API Key의 '=' 인코딩 방지를 위한 필수 설정
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.webClient = webClient.mutate()
                .uriBuilderFactory(factory)
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public Mono<Long> getMonthlySalaryByAge(AgeRange ageRange) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "getList")
                        .queryParam("apiKey", apiKey)
                        .queryParam("orgId", "118")
                        .queryParam("tblId", "DT_118N_LCE0004")
                        .queryParam("itmId", "13103732814DD_5")
                        .queryParam("objL1", "13102732817TYPES.00")
                        .queryParam("objL2", ageRange.getCode())
                        .queryParam("objL3", "").queryParam("objL4", "")
                        .queryParam("objL5", "").queryParam("objL6", "")
                        .queryParam("objL7", "").queryParam("objL8", "")
                        .queryParam("format", "json")
                        .queryParam("jsonVD", "Y")
                        .queryParam("prdSe", "Y")
                        .queryParam("newEstPrdCnt", "1")
                        .build())
                .retrieve()
                // 1. 명찰(Header)이 무엇이든 상관없이 일단 글자(String)로 다 가져옵니다.
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        // 2.ObjectMapper를 사용해 수동으로 List<KosisIncomeResponse>로 변환합니다.
                        List<KosisIncomeResponse> responses = objectMapper.readValue(json,
                                new TypeReference<List<KosisIncomeResponse>>() {});

                        return responses.isEmpty() ? Mono.empty() : Mono.just(responses.get(0));
                    } catch (Exception e) {
                        // 파싱 실패 시 에러 로그
                        return Mono.error(new RuntimeException("데이터 변환 실패: " + e.getMessage()));
                    }
                })
                // 3. 변환된 객체에서 금액 추출
                .map(KosisIncomeResponse::getConvertedAmount)
                .doOnNext(amount -> System.out.println("💰 최종 환산 금액: " + amount + "원"))
                .defaultIfEmpty(0L)
                .onErrorReturn(0L);
    }
}