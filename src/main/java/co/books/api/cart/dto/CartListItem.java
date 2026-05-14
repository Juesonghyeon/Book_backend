package co.books.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 장바구니 목록 항목 DTO. */
@Getter
@AllArgsConstructor
public class CartListItem {
    private String bookId;
    private String title;
    private String author;
    private Integer quantity;
    private Integer originalPrice;
    private Integer salePrice;
    private String imageUrl;
}