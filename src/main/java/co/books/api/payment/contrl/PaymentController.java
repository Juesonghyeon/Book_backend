package co.books.api.payment.contrl;

import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 결제 컨트롤러. */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 클라이언트가 전달한 paymentId/orderId 를 포트원 API 로 검증한 뒤
     * 결제 결과를 DB 에 저장하고 상태를 반환한다.
     */
    @PostMapping("/complete")
    public ResponseEntity<PaymentCompleteResponse> complete(
            @Valid @RequestBody PaymentCompleteRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(paymentService.complete(request, authentication.getName()));
    }
}