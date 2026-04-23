package codebadger.virtual_launch.common.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig { // 서버와 서버 간의 통신 진행

    @Bean
    public WebClient webClient() {

        // HTTP 클라이언트 설정
        HttpClient httpClient = HttpClient.create()
                // 서버 연결까지 걸리는 최대 시간을 10초로 설정
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(conn -> conn
                        // 서버와 연결된 후, 응답을 기다리는 최대 시간을 10초로 설정
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        // 서버와 연결된 후, 요청을 보내는 최대 시간을 10초로 설정
                        .addHandlerLast(new WriteTimeoutHandler(10)));


        return WebClient.builder()
                // 기본 데이터 타입 JSON
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // HTTP 클라이언트 설정 적용
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
