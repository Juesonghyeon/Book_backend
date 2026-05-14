package co.books.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/** 포트원 WebClient 빈 설정. */
@Configuration
public class PortOneWebClientConfig {

    @Value("${portone.api-base-url}")
    private String apiBaseUrl;

    @Bean(name = "portOneWebClient")
    public WebClient portOneWebClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }
}