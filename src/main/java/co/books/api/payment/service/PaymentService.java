package co.books.api.payment.service;

import co.books.api.common.exception.ForbiddenException;
import co.books.api.common.exception.NotFoundException;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderStatus;
import co.books.api.order.repo.OrderRepository;
import co.books.api.payment.client.PortOneApiClient;
import co.books.api.payment.client.PortOnePaymentResponse;
import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.entity.PaymentEntity;
import co.books.api.payment.entity.PaymentStatus;
import co.books.api.payment.repo.PaymentRepository;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

/** 결제 서비스. */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PortOneApiClient portOneApiClient;
    private final ObjectMapper objectMapper;

    /**
     * 포트원 결제 완료를 검증하고 DB 에 저장한다.
     * 멱등성 보장: 동일 paymentId 재호출 시 기존 결과를 반환한다.
     */
    @Transactional
    public PaymentCompleteResponse complete(PaymentCompleteRequest req, String userId) {
        if (!req.getPaymentId().equals(req.getOrderId())) {
            throw new IllegalArgumentException("paymentId 와 orderId 가 일치하지 않습니다.");
        }

        // 멱등성: 이미 처리된 결제면 기존 결과 반환
        Optional<PaymentEntity> existing = paymentRepository.findById(req.getPaymentId());
        if (existing.isPresent()) {
            return PaymentCompleteResponse.from(existing.get());
        }

        // 주문 조회 + 본인 확인
        OrderEntity order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("본인의 주문이 아닙니다.");
        }

        // 포트원 API 로 결제 정보 조회
        PortOnePaymentResponse pg = portOneApiClient.getPayment(req.getPaymentId());

        // 금액 검증 (위변조 방지)
        if (pg.getAmount() == null || pg.getAmount().getTotal() != order.getTotalAmount()) {
            throw new IllegalStateException("결제 금액이 주문 금액과 일치하지 않습니다.");
        }

        // payments 테이블에 저장
        PaymentEntity payment = PaymentEntity.from(req.getPaymentId(), order, pg, objectMapper);
        paymentRepository.save(payment);

        // 주문 상태 갱신
        order.updateStatus(mapToOrderStatus(pg.getStatus()));

        // 결제 실패 시 포인트 환불
        if (PaymentStatus.FAILED.name().equals(pg.getStatus()) && order.getUsedPoints() > 0) {
            refundPoints(userId, order.getUsedPoints());
        }

        return PaymentCompleteResponse.from(payment);
    }

    private void refundPoints(String userId, int points) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        user.setPoints(user.getPoints() + points);
    }

    private OrderStatus mapToOrderStatus(String pgStatus) {
        return switch (pgStatus) {
            case "PAID"                   -> OrderStatus.PAID;
            case "FAILED"                 -> OrderStatus.FAILED;
            case "VIRTUAL_ACCOUNT_ISSUED" -> OrderStatus.PENDING;
            default                       -> OrderStatus.PENDING;
        };
    }
}