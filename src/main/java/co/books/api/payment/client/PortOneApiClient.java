package co.books.api.payment.client;

import co.books.api.common.exception.PortOneApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** 포트원 REST API 클라이언트. */
@Component
@RequiredArgsConstructor
public class PortOneApiClient {

    @Qualifier("portOneWebClient")
    private final WebClient portOneWebClient;

    @Value("${portone.api-secret}")
    private String apiSecret;

    /** paymentId 로 포트원 결제 정보를 조회한다. */
    public PortOnePaymentResponse getPayment(String paymentId) {
        return portOneWebClient.get()
                .uri("/payments/{paymentId}", paymentId)
                .header(HttpHeaders.AUTHORIZATION, "PortOne " + apiSecret)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .map(body -> new PortOneApiException("포트원 오류: " + body)))
                .bodyToMono(PortOnePaymentResponse.class)
                .block();
    }
}