package co.books.api.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** 결제 완료 검증 요청 DTO. */
@Getter
public class PaymentCompleteRequest {

    @NotBlank
    private String paymentId;

    @NotBlank
    private String orderId;
}