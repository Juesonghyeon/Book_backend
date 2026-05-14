package co.books.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 주문 생성 응답 DTO. */
@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private String orderId;
    private Integer totalAmount;
    private String orderName;
}