package codebadger.virtual_launch.domain.persona.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KosisApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    public KosisApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${kosis.api.key}") String apiKey, // properties에서 키 주입 🔑
            @Value("${kosis.api.url}") String baseUrl  // properties에서 URL 주입 🌐
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

}
