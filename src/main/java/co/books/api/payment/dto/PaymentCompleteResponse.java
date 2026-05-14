package co.books.api.payment.dto;

import co.books.api.payment.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 결제 완료 응답 DTO. */
@Getter
@AllArgsConstructor
public class PaymentCompleteResponse {

    private String status;
    private String paymentId;
    private String message;

    public static PaymentCompleteResponse from(PaymentEntity payment) {
        String message = switch (payment.getStatus()) {
            case PAID                   -> "결제가 완료되었습니다.";
            case FAILED                 -> "결제가 실패되었습니다.";
            case VIRTUAL_ACCOUNT_ISSUED -> "가상계좌가 발급되었습니다.";
        };
        return new PaymentCompleteResponse(
                payment.getStatus().name(),
                payment.getPaymentId(),
                message
        );
    }
}