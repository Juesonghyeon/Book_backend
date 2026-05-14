package co.books.api.cart.dto;

import lombok.Getter;

/** 장바구니 API 공통 응답 DTO. */
@Getter
public class CartApiResponse {

    private final int code;
    private final String message;

    private CartApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CartApiResponse ok() {
        return new CartApiResponse(200, "장바구니 등록이 되었습니다.");
    }

    public static CartApiResponse error() {
        return new CartApiResponse(500, "장바구니 등록이 실패되었습니다.");
    }

    public static CartApiResponse updateOk() {
        return new CartApiResponse(200, "수정이 되었습니다.");
    }

    public static CartApiResponse updateError() {
        return new CartApiResponse(500, "수정이 실패되었습니다.");
    }

    public static CartApiResponse deleteOk() {
        return new CartApiResponse(200, "삭제 되었습니다.");
    }

    public static CartApiResponse deleteError() {
        return new CartApiResponse(500, "삭제 실패되었습니다.");
    }
}