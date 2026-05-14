package co.books.api.payment.entity;

import co.books.api.order.entity.OrderEntity;
import co.books.api.payment.client.PortOnePaymentResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

/** 결제 엔티티. */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "tx_id", length = 100)
    private String txId;

    @Column(name = "channel_key", nullable = false, length = 100)
    private String channelKey;

    @Column(name = "pg_provider", length = 50)
    private String pgProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_method", length = 30)
    private PayMethod payMethod;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(length = 10, nullable = false)
    private String currency = "KRW";

    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    @Column(name = "fail_reason", columnDefinition = "TEXT")
    private String failReason;

    @Column(name = "fail_code", length = 50)
    private String failCode;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** 포트원 응답과 주문 정보로 PaymentEntity 를 생성하는 팩토리 메서드. */
    public static PaymentEntity from(String paymentId,
                                     OrderEntity order,
                                     PortOnePaymentResponse pg,
                                     ObjectMapper objectMapper) {
        PaymentEntity p = new PaymentEntity();
        p.setPaymentId(paymentId);
        p.setOrderId(order.getOrderId());
        p.setUserId(order.getUserId());
        p.setTxId(pg.getTransactionId());

        if (pg.getChannel() != null) {
            p.setChannelKey(pg.getChannel().getKey() != null ? pg.getChannel().getKey() : "");
            p.setPgProvider(pg.getChannel().getPgProvider());
        } else {
            p.setChannelKey("");
        }

        p.setStatus(PaymentStatus.valueOf(pg.getStatus()));
        p.setPayMethod(mapPayMethod(pg.getMethod()));

        if (pg.getAmount() != null) {
            p.setTotalAmount(pg.getAmount().getTotal());
            p.setCurrency(pg.getAmount().getCurrency() != null ? pg.getAmount().getCurrency() : "KRW");
        } else {
            p.setTotalAmount(0);
        }

        p.setOrderName(pg.getOrderName() != null ? pg.getOrderName() : order.getOrderName());

        if (pg.getFailure() != null) {
            p.setFailReason(pg.getFailure().getReason());
            p.setFailCode(pg.getFailure().getPgCode());
        }

        p.setPaidAt(pg.getPaidAt());

        try {
            p.setRawResponse(objectMapper.writeValueAsString(pg));
        } catch (Exception e) {
            p.setRawResponse("{}");
        }

        return p;
    }

    private static PayMethod mapPayMethod(PortOnePaymentResponse.Method method) {
        if (method == null || method.getType() == null) return null;
        return switch (method.getType()) {
            case "PaymentMethodCard"           -> PayMethod.CARD;
            case "PaymentMethodVirtualAccount" -> PayMethod.VIRTUAL_ACCOUNT;
            case "PaymentMethodTransfer"       -> PayMethod.TRANSFER;
            case "PaymentMethodMobile"         -> PayMethod.MOBILE;
            case "PaymentMethodEasyPay"        -> PayMethod.EASY_PAY;
            default                            -> null;
        };
    }
}