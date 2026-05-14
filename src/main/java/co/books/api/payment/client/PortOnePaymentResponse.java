package co.books.api.payment.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 포트원 V2 결제 조회 응답 DTO.
 * Spring Boot 기본 ObjectMapper 설정(FAIL_ON_UNKNOWN_PROPERTIES=false)으로 알 수 없는 필드를 무시한다.
 */
@Getter
@NoArgsConstructor
public class PortOnePaymentResponse {

    /** PAID | FAILED | VIRTUAL_ACCOUNT_ISSUED | ... */
    private String status;

    private String transactionId;

    private Channel channel;

    private Method method;

    private Amount amount;

    private String orderName;

    private OffsetDateTime paidAt;

    private Failure failure;

    @Getter
    @NoArgsConstructor
    public static class Channel {
        private String key;
        private String pgProvider;
    }

    @Getter
    @NoArgsConstructor
    public static class Method {
        /** PaymentMethodCard | PaymentMethodEasyPay | ... */
        private String type;
    }

    @Getter
    @NoArgsConstructor
    public static class Amount {
        private Integer total;
        private Integer taxFree;
        private Integer vat;
        private String currency;
    }

    @Getter
    @NoArgsConstructor
    public static class Failure {
        private String reason;
        private String pgCode;
    }
}