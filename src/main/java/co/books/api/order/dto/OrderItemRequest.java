package co.books.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** 주문 상품 항목 요청 DTO. */
@Getter
public class OrderItemRequest {

    @NotBlank
    private String bookId;

    @Min(1)
    private Integer quantity;

    @Min(0)
    private Integer priceAtPurchase;
}