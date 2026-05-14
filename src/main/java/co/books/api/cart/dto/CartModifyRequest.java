package co.books.api.cart.dto;

import lombok.Getter;

/** 장바구니 수량 수정 요청 DTO. */
@Getter
public class CartModifyRequest {
    private String bookId;
    private Integer quantity;
}