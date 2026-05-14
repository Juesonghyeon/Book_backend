package co.books.api.cart.dto;

import lombok.Getter;

/** 장바구니 등록 요청 DTO. */
@Getter
public class CartRequest {
    private String bookId;
    private Integer quantity;
}